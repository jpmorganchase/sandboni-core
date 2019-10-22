package com.sandboni.core.engine;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

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
        Assert.assertNull(args.getFilter());
        Assert.assertNull(args.getTrackingSort());
        Assert.assertFalse(args.isRunSelectiveMode());
        Assert.assertEquals(".", args.getReportDir());
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
                .srcLocation(new HashSet<>(Collections.singletonList("a\\b\\c")))
                .testLocation(new HashSet<>(Collections.singletonList("a\\b\\c")))
                .build();
        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(1, args.getSrcLocation().size());
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(1, args.getTestLocation().size());
    }

    @Test
    public void setMultipleLocation() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .srcLocation(new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "g\\h\\i")))
                .testLocation(new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "g\\h\\i")))
                .build();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(3, args.getSrcLocation().size());
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(3, args.getTestLocation().size());
    }

    @Test
    public void setMultipleSimilarLocation() {
        Arguments args = Arguments.builder()
                .fromChangeId("1")
                .toChangeId("2")
                .repository(".")
                .srcLocation(new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "d\\e\\f", "g\\h\\i", "a\\b\\c")))
                .testLocation(new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "d\\e\\f", "g\\h\\i", "a\\b\\c")))
                .build();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(3, args.getSrcLocation().size());
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(3, args.getTestLocation().size());
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
}
