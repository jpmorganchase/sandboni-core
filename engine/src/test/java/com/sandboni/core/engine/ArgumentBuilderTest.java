package com.sandboni.core.engine;

import org.junit.Assert;
import org.junit.Test;

public class ArgumentBuilderTest {

    @Test
    public void testDefaultBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .reportDir(".")
                .build();

        Assert.assertNotNull(args);
        Assert.assertEquals("1", args.getFromChangeId());
        Assert.assertEquals("2", args.getToChangeId());
        Assert.assertEquals(".", args.getRepository());
        Assert.assertNull(args.getSrcLocation());
        Assert.assertNull(args.getTestLocation());
        Assert.assertNull(args.getDependencies());
        Assert.assertNull(args.getFilter());
        Assert.assertNull(args.getTrackingSort());
        Assert.assertFalse(args.isRunSelectiveMode());
        Assert.assertEquals(".", args.getReportDir());
        Assert.assertNull(args.getSeloniFilePath());
        Assert.assertFalse(args.isEnablePreview());
        Assert.assertNull(args.getApplicationId());
        Assert.assertNull(args.getOutputFormat());
        Assert.assertFalse(args.isRunAllExternalTests());
        Assert.assertFalse(args.isGitCache());
        Assert.assertFalse(args.isCoreCache());
        Assert.assertNull(args.getStage());
        Assert.assertNull(args.getAlwaysRunAnnotation());
        Assert.assertFalse(args.isIgnoreUnsupportedFiles());

    }

    @Test
    public void testSelectiveModeBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .runSelectiveMode(true)
                .build();

        Assert.assertTrue(args.isRunSelectiveMode());
    }

    @Test
    public void testSeloniFilepathBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .seloniFilePath("seloni/file/path")
                .build();

        Assert.assertEquals("seloni/file/path", args.getSeloniFilePath());
    }

    @Test
    public void testSetFilterBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .filter("***")
                .build();

        Assert.assertEquals("***", args.getFilter());
    }

    @Test
    public void testSetTrackingSortBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .trackingSort("desc")
                .build();

        Assert.assertEquals("desc", args.getTrackingSort());
    }

    @Test
    public void setOneLocation() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .srcLocation(new String[] {"a\\b\\c"})
                .testLocation(new String[] {"a\\b\\c"})
                .build();
        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(1, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(1, args.getTestLocation().length);
    }

    @Test
    public void setMultipleLocation() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .srcLocation(new String[] {"a\\b\\c", "d\\e\\f", "g\\h\\i"})
                .testLocation(new String[] {"a\\b\\c", "d\\e\\f", "g\\h\\i"})
                .build();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(3, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(3, args.getTestLocation().length);
    }

    @Test
    public void setSingleOutputFile() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .outputFormat("csv")
                .build();

        Assert.assertEquals("csv", args.getOutputFormat());
    }

    @Test
    public void testEnablePreviewBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .enablePreview(true)
                .build();

        Assert.assertTrue(args.isEnablePreview());
    }

    @Test
    public void testUseScopeResolverFromCliGitDiffBuilder() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .useCliDiff(true)
                .build();

        Assert.assertTrue(args.isUseCliDiff());
    }
}
