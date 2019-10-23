package com.sandboni.core.engine;

import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.finder.bcel.BcelFinder;
import com.sandboni.core.engine.finder.bcel.CachedBcelFinder;
import com.sandboni.core.engine.finder.cucumber.CucumberFeatureFinder;
import com.sandboni.core.engine.result.ChangeDetectorResultMock;
import com.sandboni.core.engine.sta.connector.Connector;
import com.sandboni.core.engine.sta.connector.HttpTemplateConnector;
import com.sandboni.core.scm.CachedRepository;
import com.sandboni.core.scm.GitRepository;
import com.sandboni.core.scm.utils.GitHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcessorBuilderTest {

    private Arguments args;

    @Before
    public void buildArguments() {
        args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(GitHelper.openCurrentFolder())
                .stage(Stage.BUILD.name())
                .runAllExternalTests(false)
                .coreCache(true)
                .gitCache(true)
                .build();
    }

    @Test
    public void testBuilderReturnsProcessor() {
        Assert.assertNotNull(args);
        Processor p = new ProcessorBuilder()
                .with(prBuilder -> prBuilder.arguments = args)
                .build();

        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getArguments());
    }

    @Test
    public void testCacheProcessor() {
        Assert.assertNotNull(args);

        Processor p = new ProcessorBuilder()
                .with(prBuilder -> prBuilder.arguments = args)
                .build();

        Assert.assertNotNull(p);
        Assert.assertTrue(p.getChangeDetector() instanceof CachedRepository);
        Assert.assertTrue(p.getFinders().stream().anyMatch(f-> f instanceof CachedBcelFinder));
        Assert.assertTrue(p.getFinders().stream().noneMatch(f-> !(f instanceof CachedBcelFinder) && f instanceof BcelFinder));
    }

    @Test
    public void testNoCacheProcessor() {
        args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(GitHelper.openCurrentFolder())
                .stage(Stage.BUILD.name())
                .runAllExternalTests(false)
                .coreCache(false)
                .gitCache(false)
                .build();

        Processor p = new ProcessorBuilder()
                .with(prBuilder -> prBuilder.arguments = args)
                .build();

        Assert.assertNotNull(p);
        Assert.assertTrue(p.getChangeDetector() instanceof GitRepository && !(p.getChangeDetector() instanceof CachedBcelFinder));
        Assert.assertTrue(p.getFinders().stream().anyMatch(f-> f instanceof BcelFinder && !(f instanceof CachedBcelFinder)));
        Assert.assertTrue(p.getFinders().stream().noneMatch(f-> f instanceof CachedBcelFinder));
    }

    @Test
    public void testSetFinders() {
        Assert.assertNotNull(args);

        Processor p = new ProcessorBuilder()
                .with(prBuilder -> {
                    prBuilder.arguments = args;
                    prBuilder.finders = new Finder[]{new CucumberFeatureFinder()};
                })
                .build();

        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getFinders());
        Assert.assertEquals(1, p.getFinders().size());
        Assert.assertTrue( p.getFinders().stream().anyMatch(f->f instanceof CucumberFeatureFinder));
    }

    @Test
    public void testSetConnectors() {
        Assert.assertNotNull(args);

        Processor p = new ProcessorBuilder()
                .with(prBuilder -> {
                    prBuilder.arguments = args;
                    prBuilder.connectors = new Connector[]{new HttpTemplateConnector()};
                })
                .build();

        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getConnectors());
        Assert.assertEquals(1, p.getConnectors().size());
        Assert.assertTrue( p.getConnectors().stream().anyMatch(c->c instanceof HttpTemplateConnector));
    }

    @Test
    public void testSetGitDetector() {
        Assert.assertNotNull(args);
        Processor p = new ProcessorBuilder()
                .with(prBuilder -> {
                    prBuilder.arguments = args;
                    prBuilder.gitDetector = new ChangeDetectorResultMock();
                })
                .build();
        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getChangeDetector());
        Assert.assertTrue( p.getChangeDetector() instanceof ChangeDetectorResultMock);
    }
}
