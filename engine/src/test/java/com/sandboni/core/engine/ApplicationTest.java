package com.sandboni.core.engine;

import com.sandboni.core.scm.utils.GitHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ApplicationTest {

    private void fillSystemProperties(){
        System.setProperty(SystemProperties.SRC_LOCATION.getName(),"./target/classes");
        System.setProperty(SystemProperties.TEST_LOCATION.getName(),"./target/test-classes");
        System.setProperty("sandboni.scm.from", "LATEST_PUSH");
        System.setProperty("sandboni.scm.to", "LOCAL_CHANGES_NOT_COMMITTED");
        System.setProperty("sandboni.scm.repository", GitHelper.openCurrentFolder());
        System.setProperty("sandboni.selectiveMode", "true");
        System.setProperty("sandboni.runAllExternaltests", "false");
        System.setProperty("sandboni.stage", Stage.BUILD.name());
        System.setProperty("sandboni.ignore.unsupported.files", "true");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSystemProperties() {
        System.clearProperty(SystemProperties.SRC_LOCATION.getName());
        System.clearProperty(SystemProperties.TEST_LOCATION.getName());
        System.clearProperty(SystemProperties.SRC_LOCATION.getName());
        System.clearProperty(SystemProperties.TEST_LOCATION.getName());
        System.clearProperty("sandboni.scm.from");
        System.clearProperty("sandboni.scm.to");
        System.clearProperty("sandboni.scm.repository");
        System.clearProperty("sandboni.selectiveMode");

        Application.main(new String[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoLocationsSystemProperty() {
        fillSystemProperties();
        System.clearProperty(SystemProperties.SRC_LOCATION.getName());
        System.clearProperty(SystemProperties.TEST_LOCATION.getName());
        Application.main(new String[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoScmFromSystemProperty() {
        fillSystemProperties();
        System.clearProperty("sandboni.scm.from");
        Application.main(new String[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoScmToSystemProperty() {
        fillSystemProperties();
        System.clearProperty("sandboni.scm.to");
        Application.main(new String[    ]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoScmRepositorySystemProperty() {
        fillSystemProperties();
        System.clearProperty("sandboni.scm.repository");
        Application.main(new String[]{});
    }

    @Test
    public void testAllPropertiesAreMatched() {
        fillSystemProperties();
        Application app = new Application();
        app.buildArguments();
        Arguments args = app.getArguments();
        Assert.assertTrue(Arrays.asList(args.getSrcLocation()).contains("./target/classes"));
        Assert.assertTrue(Arrays.asList(args.getTestLocation()).contains("./target/test-classes"));
        Assert.assertEquals("LATEST_PUSH", args.getFromChangeId());
        Assert.assertEquals("LOCAL_CHANGES_NOT_COMMITTED", args.getToChangeId());
        Assert.assertEquals(GitHelper.openCurrentFolder(), args.getRepository());
        Assert.assertTrue(args.isRunSelectiveMode());
        Assert.assertFalse(args.isRunAllExternalTests());
        Assert.assertTrue(args.isIgnoreUnsupportedFiles());
    }

    @Test
    public void testWithAllProperties(){
        fillSystemProperties();

        Application app = new Application();
        app.buildArguments();
        Arguments args = app.getArguments();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertSame(1, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertSame(1, args.getTestLocation().length);

        Assert.assertEquals("LATEST_PUSH", args.getFromChangeId());
        Assert.assertEquals("LOCAL_CHANGES_NOT_COMMITTED", args.getToChangeId());
        Assert.assertEquals(GitHelper.openCurrentFolder(), args.getRepository());
        Assert.assertTrue(args.isRunSelectiveMode());
        Assert.assertTrue(args.isIgnoreUnsupportedFiles());
    }
}
