package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.JarFinder;
import com.sandboni.core.engine.finder.bcel.BcelFinder;
import com.sandboni.core.engine.finder.bcel.CachedBcelFinder;
import com.sandboni.core.engine.finder.bcel.ClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.*;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerClassVisitor;
import com.sandboni.core.engine.finder.cucumber.CucumberFeatureFinder;
import com.sandboni.core.engine.finder.explicit.ExplicitFinder;
import com.sandboni.core.engine.sta.graph.LinkFactory;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.CucumberJavaConnector;
import com.sandboni.core.engine.sta.connector.HttpTemplateConnector;
import com.sandboni.core.scm.CachedRepository;
import com.sandboni.core.scm.GitInterface;
import com.sandboni.core.scm.GitRepository;

import java.util.Objects;
import java.util.function.Consumer;

public class ProcessorBuilder implements BuilderPattern<Processor, ProcessorBuilder>{

    public Arguments arguments;
    public GitInterface gitDetector;
    public Finder[] finders;
    public Connector[] connectors;

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
                                new JarFinder(getVisitors())},
                Objects.nonNull(this.connectors) ? this.connectors : getConnectors());
    }

    private static Connector[] getConnectors() {
        return new Connector[]{new HttpTemplateConnector(), new CucumberJavaConnector()};
    }

    private ClassVisitor[] getVisitors() {
        return new ClassVisitor[]{
                new AffectedClassVisitor(),
                new CallerClassVisitor(),
                new ImplementingClassVisitor(),
                new InheritanceClassVisitor(),
                new JavaxControllerClassVisitor(),
                new SpringControllerClassVisitor(),
                new TestClassVisitor(getIncludeAnnotations())};
    }

    private String[] getIncludeAnnotations() {
        return arguments.getIncludeAnnotations() == null ?
                new String[0] : arguments.getIncludeAnnotations().toArray(new String[0]);
    }

}
