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
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.operation.GraphOperations;
import com.sandboni.core.engine.utils.StringUtil;
import com.sandboni.core.scm.utils.timing.SWConsts;
import com.sandboni.core.scm.utils.timing.StopWatch;
import com.sandboni.core.scm.utils.timing.StopWatchManager;
import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.scope.analysis.ChangeScopeAnalyzer;
import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
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
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_RESULT_GENERATOR_IMPL, "ALL").start();
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_RESULT_GENERATOR_IMPL, "getBuilder").start();
        Builder builder = builderSupplier.get();
        sw1.stop();
        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_RESULT_GENERATOR_IMPL, "builder.getGraph").start();
        DirectedGraph<Vertex, Edge> graph = builder.getGraph();
        sw2.stop();
        StopWatch sw3 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_RESULT_GENERATOR_IMPL, "contextSupplier").start();
        Context context = contextSupplier.get();
        sw3.stop();
        StopWatch sw4 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_RESULT_GENERATOR_IMPL, "create resultGenerator").start();
        ResultGenerator resultGenerator = new ResultGenerator(new GraphOperations(graph, context), arguments, builder.getFilterIndicator());
        sw4.stop();
        swAll.stop();
        return resultGenerator;
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
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER, "getContext").start();
        Context context = contextSupplier.get();
        sw1.stop();
        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER, "getBuilder(context)").start();
        Builder builder = getBuilder(context);
        sw2.stop();
        return builder;
    }

    /**
     * Returns true if first: is build stage or runAllExternalTests is false
     * then: (a) no change was made (b) change was made and contains at least one java file (not just cnfg files)
     *
     * @param changeScope the change scope
     * @return boolean
     */
    private boolean proceed(ChangeScope<Change> changeScope) {
        StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_PROCEED, "ALL").start();
        boolean proceed = (!isRunAllExternalTests() || !isIntegrationStage())
                && (arguments.isRunSelectiveMode()
                || (ChangeScopeAnalyzer.onlySupportedFiles(changeScope, getSupportedFiles())
                && ChangeScopeAnalyzer.analyzeConfigurationFiles(changeScope, getBuildFiles())));
        swAll.stop();
        return proceed;
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
            StopWatch swAll = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_SCOPE, "ALL").start();
            ChangeScope<Change> changes = changeDetector.getChanges(arguments.getFromChangeId(), arguments.getToChangeId());
            swAll.stop();
            log.info("[{}] Change scope retrieved in {} milliseconds", Thread.currentThread().getName(), elapsedTime(start));
            return changes;
        } catch (SourceControlException e) {
            throw new ParseRuntimeException(e);
        }
    }

    private Context createContext() {
        StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_CREATE_CTX, "getChangeScope").start();
        ChangeScope<Change> changes = changeScopeSupplier.get();
        sw1.stop();
        StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_CREATE_CTX, "createContext Object").start();
        Context context = new Context(arguments.getApplicationId(), arguments.getSrcLocation(), arguments.getTestLocation(),
                arguments.getDependencies(), arguments.getFilter(), changes, arguments.getAlwaysRunAnnotation(),
                arguments.getSeloniFilePath(), arguments.isEnablePreview());
        sw2.stop();
        return context;
    }

    private Builder getBuilder(Context context) {
        //proceed iff change scope contains at least one java file
        if (context.getChangeScope().isEmpty()) {
            log.info("There are no changes in this project");
            return new Builder(context, FilterIndicator.NONE);
        } else {
            if (proceed(context.getChangeScope())) {
                log.info("Found changes: {}", context.getChangeScope());
                log.info("Sandboni will include only '.java' and '.feature' files for filtering");

                StopWatch sw1 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER_CTX, "filter change scope").start();
                context.getChangeScope().include(FileExtensions.JAVA, FileExtensions.FEATURE);
                sw1.stop();

                // at least one file in the change scope exists in this module source code.
                StopWatch sw2 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER_CTX, "moduleContainsChanges").start();
                boolean moduleContainsChanges = moduleContainsChanges(context.getChangeScope());
                sw2.stop();
                if (!moduleContainsChanges) {
                    log.info("Changed files are not included in this module, skipping Sandboni");
                    return new Builder(context, FilterIndicator.NONE);
                }

                Instant start = Instant.now();
                StopWatch sw3 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER_CTX, "executeFinders (scope)").start();
                executeFinders(context);
                sw3.stop();
                Instant finish = Instant.now();
                log.debug("....Finders execution total time: {}", Duration.between(start, finish).toMillis());

                start = Instant.now();
                StopWatch sw4 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER_CTX, "execute connectors").start();
                connectors.parallelStream().filter(c -> c.proceed(context)).forEach(c -> c.connect(context));
                sw4.stop();
                finish = Instant.now();

                log.debug("....Connectors execution total time: {}", Duration.between(start, finish).toMillis());

                if (!StringUtil.isEmptyOrNull(arguments.getSeloniFilePath())) {
                    return new Builder(context, FilterIndicator.SELECTIVE_EXTERNAL);
                }
            } else if (isRunAllExternalTests() && isIntegrationStage()) {
                log.info("Running All External Tests");
                StopWatch sw5 = StopWatchManager.getStopWatch(this.getClass().getSimpleName(), SWConsts.METHOD_NAME_GET_BUILDER_CTX, "executeFinders (all external tests)").start();
                executeFinders(context);
                sw5.stop();
                return new Builder(context, FilterIndicator.ALL_EXTERNAL);
            } else { //only cnfg files
                log.info("Found changes: {}", context.getChangeScope());
                log.info(" ** Configuration files or files outside Sandboni's scope were changed; All tests will be executed ** ");
                return new Builder(context, FilterIndicator.ALL);
            }
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
        finders.parallelStream().forEach(f -> {
            Context localContext = context.getLocalContext();
            f.findSafe(localContext);
            context.addLinks(localContext.getLinks().toArray(Link[]::new));
        });
    }

    private static String[] getBuildFiles() {
        return new String[]{"pom.xml", "build.gradle"};
    }

    private static String[] getSupportedFiles() {
        return Stream.of(FileExtensions.values()).map(FileExtensions::extension).toArray(String[]::new);
    }
}