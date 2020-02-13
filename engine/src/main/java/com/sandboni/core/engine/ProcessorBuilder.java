package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.filter.ChangeScopeFilter;
import com.sandboni.core.engine.filter.ScopeFilter;
import com.sandboni.core.engine.finder.JarFinder;
import com.sandboni.core.engine.finder.bcel.BcelFinder;
import com.sandboni.core.engine.finder.bcel.CachedBcelFinder;
import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.*;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerClassVisitor;
import com.sandboni.core.engine.finder.cucumber.CucumberFeatureFinder;
import com.sandboni.core.engine.finder.explicit.ExplicitFinder;
import com.sandboni.core.engine.sta.connector.*;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.scm.CachedRepository;
import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ProcessorBuilder implements BuilderPattern<Processor, ProcessorBuilder>{

    public Arguments arguments;
    public GitInterface gitDetector;
    public Finder[] finders;
    public Connector[] connectors;
    public ScopeFilter<ChangeScope<Change>, Set<File>> scopeFilter;

    @Override
    public ProcessorBuilder with(
            Consumer<ProcessorBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }

    @SuppressWarnings("squid:S3358")
    public Processor build() {
        BcelFinder bcelFinder = arguments.isCoreCache() ?
                new CachedBcelFinder(getVisitors()) : new BcelFinder(getVisitors());
        LinkFactory.clear(arguments.getApplicationId());

        return new Processor(Objects.requireNonNull(this.arguments),
                Objects.nonNull(gitDetector) ? gitDetector :
                        arguments.isGitCache() ? new CachedRepository(this.arguments.getRepository()) :
                                new GitRepository(this.arguments.getRepository()),
                Objects.nonNull(finders) ? this.finders :
                        new Finder[]{new ExplicitFinder(),
                                bcelFinder,
                                new CucumberFeatureFinder(),
                                new JarFinder()},
                Objects.nonNull(this.connectors) ? this.connectors : getConnectors(),
                getScopeFilter());
    }

    private ScopeFilter<ChangeScope<Change>, Set<File>> getScopeFilter() {
        return this.scopeFilter == null ? new ChangeScopeFilter() : this.scopeFilter;
    }

    private static Connector[] getConnectors() {
        return new Connector[]{new HttpTemplateConnector(), new CucumberJavaConnector(), new TestSuiteConnector()};
    }

    public static ClassVisitor[] getVisitors() {
        return new ClassVisitor[]{
                new AffectedClassVisitor(),
                new CallerClassVisitor(),
                new ImplementingClassVisitor(),
                new InheritanceClassVisitor(),
                new JavaxControllerClassVisitor(),
                new SpringControllerClassVisitor(),
                new TestClassVisitor()};
    }
}
