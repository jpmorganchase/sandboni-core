package com.sandboni.core.engine;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import com.sandboni.core.engine.filter.ScopeFilter;
import com.sandboni.core.engine.render.banner.BannerRenderService;
import com.sandboni.core.engine.result.FilterIndicator;
import com.sandboni.core.engine.sta.Builder;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.executor.FinderExecutor;
import com.sandboni.core.engine.sta.operation.GraphOperations;
import com.sandboni.core.engine.utils.StringUtil;
import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.scope.analysis.ChangeScopeAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sandboni.core.engine.utils.TimeUtils.elapsedTime;

public class Processor {

    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private final Arguments arguments;
    private final GitInterface changeDetector;
    private final Collection<Finder> finders;
    private final Collection<Connector> connectors;

    private final Supplier<Context> contextSupplier = new CachingSupplier<>(this::getContext);
    private final Supplier<ChangeScope<Change>> changeScopeSupplier = new CachingSupplier<>(this::getScope);
    private final Supplier<Builder> builderSupplier = new CachingSupplier<>(this::getBuilder);
    private final Supplier<ResultGenerator> resultGeneratorSupplier = new CachingSupplier<>(this::getResultGeneratorImpl);
    private final ScopeFilter<ChangeScope<Change>, Set<File>> scopeFilter;

    Processor(Arguments arguments, GitInterface changeDetector, Finder[] finders, Connector[] connectors,
              ScopeFilter<ChangeScope<Change>, Set<File>> scopeFilter) {
        this.arguments = arguments;
        this.changeDetector = changeDetector;
        this.finders = Collections.unmodifiableCollection(Arrays.asList(finders));
        this.connectors = Collections.unmodifiableCollection(Arrays.asList(connectors));
        this.scopeFilter = scopeFilter;

        //rendering Sandboni logo
        new BannerRenderService().render();
    }

    GitInterface getChangeDetector() {
        return changeDetector;
    }

    Collection<Finder> getFinders() {
        return finders;
    }

    Collection<Connector> getConnectors() {
        return connectors;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public ResultGenerator getResultGenerator() {
        return resultGeneratorSupplier.get();
    }

    private ResultGenerator getResultGeneratorImpl() {
        return new ResultGenerator(new GraphOperations(builderSupplier.get().getGraph(), contextSupplier.get()), arguments, builderSupplier.get().getFilterIndicator());
    }

    private Context getContext() {
        try {
            Instant start = Instant.now();
            Context context = createContext();
            Instant finish = Instant.now();
            log.debug("Context creation execution total time: {}", Duration.between(start, finish).toMillis());
            return context;
        } catch (Exception e) {
            throw new ParseRuntimeException(e);
        }
    }

    private Builder getBuilder() {
        return getBuilder(contextSupplier.get());
    }

    private boolean proceed(ChangeScope<Change> changeScope) {
        return (!isRunAllExternalTests() || !isIntegrationStage())
                && (arguments.isRunSelectiveMode()
                || (ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, getBuildFiles()))
                && (arguments.isIgnoreUnsupportedFiles()
                || ChangeScopeAnalyzer.onlySupportedFiles(changeScope, getSupportedFiles())));
    }

    private boolean isRunAllExternalTests() {
        return arguments.isRunAllExternalTests();
    }

    private boolean isIntegrationStage() {
        return arguments.getStage().equals(Stage.INTEGRATION.name());
    }

    public ChangeScope<Change> getScope() {
        log.info("[{}] Getting change scope", Thread.currentThread().getName());
        long start = System.nanoTime();
        try {
            ChangeScope<Change> changes = changeDetector.getChanges(arguments.getFromChangeId(), arguments.getToChangeId());
            log.info("[{}] Change scope retrieved in {} milliseconds", Thread.currentThread().getName(), elapsedTime(start));
            return changes;
        } catch (SourceControlException e) {
            throw new ParseRuntimeException(e);
        }
    }

    private Context createContext() {
        return new Context(arguments.getApplicationId(), arguments.getSrcLocation(), arguments.getTestLocation(),
                arguments.getDependencies(), arguments.getFilter(), changeScopeSupplier.get(), arguments.getAlwaysRunAnnotation(),
                arguments.getSeloniFilePath(), arguments.isEnablePreview());
    }

    private Builder getBuilder(Context context) {
        //proceed iff change scope contains at least one java file
        if (context.getChangeScope().isEmpty()) {
            log.info("There are no changes in this project");
            return new Builder(context, FilterIndicator.NONE);
        } else if (proceed(context.getChangeScope())) {
            log.info("Found changes: {}", context.getChangeScope());
            log.info("Sandboni will include only '.java' and '.feature' files for filtering");

            context.getChangeScope().include(FileExtensions.JAVA, FileExtensions.FEATURE);

            // at least one file in the change scope exists in this module source code.
            if (!moduleContainsChanges(context.getChangeScope())) {
                log.info("Changed files are not included in this module, skipping Sandboni");
                return new Builder(context, FilterIndicator.NONE);
            }

            Instant start = Instant.now();
            executeFinders(context);
            Instant finish = Instant.now();
            log.debug("....Finders execution total time: {}", Duration.between(start, finish).toMillis());

            start = Instant.now();
            connectors.parallelStream().filter(c -> c.proceed(context)).forEach(c -> c.connect(context));
            finish = Instant.now();

            log.debug("....Connectors execution total time: {}", Duration.between(start, finish).toMillis());

            if (!StringUtil.isEmptyOrNull(arguments.getSeloniFilePath())){
                return new Builder(context, FilterIndicator.SELECTIVE_EXTERNAL);
            }
        } else if (isRunAllExternalTests() && isIntegrationStage()) {
            log.info("Running All External Tests");
            executeFinders(context);
            return new Builder(context, FilterIndicator.ALL_EXTERNAL);
        } else { //only cnfg files
            log.info("Found changes: {}", context.getChangeScope());
            log.info(" ** Configuration files or files outside Sandboni's scope were changed; All tests will be executed ** ");
            return new Builder(context, FilterIndicator.ALL);
        }
        return new Builder(context);
    }

    private boolean moduleContainsChanges(ChangeScope<Change> changeScope) {
        return scopeFilter.isInScope(changeScope,
                Stream.of(arguments.getSrcLocation())
                        .map(File::new)
                        .collect(Collectors.toSet()));
    }

    private void executeFinders(Context context) {
        FinderExecutor finderExecutor = new FinderExecutor(context);
        finderExecutor.execute(finders);
    }

    private static String[] getBuildFiles() {
        return new String[]{"pom.xml", "build.gradle"};
    }

    private static String[] getSupportedFiles() {
        return Stream.of(FileExtensions.values()).map(FileExtensions::extension).toArray(String[]::new);
    }
}