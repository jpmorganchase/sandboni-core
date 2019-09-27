package com.sandboni.core.engine;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ArgumentBuilderTest {

    @Test
    public void testDefaultBuilder(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
        }).build();

        Assert.assertNotNull(args);
        Assert.assertEquals("1", args.getFromChangeId());
        Assert.assertEquals("2", args.getToChangeId());
        Assert.assertEquals(".", args.getRepository());
        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(0, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(0, args.getTestLocation().length);
        Assert.assertNull( args.getFilter());
        Assert.assertNull( args.getTrackingSort());
        Assert.assertFalse( args.isRunSelectiveModeIfBuildFileHasChanged());
        Assert.assertEquals(".", args.getReportDir());
    }


    @Test
    public void testSelectiveModeBuilder(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.selectiveMode = true;
        }).build();

        Assert.assertTrue( args.isRunSelectiveModeIfBuildFileHasChanged());
    }

    @Test
    public void testSetFilterBuilder(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.filter = "***";
        }).build();

        Assert.assertEquals("***", args.getFilter());
    }

    @Test
    public void testSetTrackingSortBuilder(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.trackingSort = "desc";
        }).build();

        Assert.assertEquals("desc", args.getTrackingSort());
    }

    @Test
    public void setOneLocation(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.srcLocations = new HashSet<>(Collections.singletonList("a\\b\\c"));
            $.testLocations = new HashSet<>(Collections.singletonList("a\\b\\c"));
        }).build();
        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(1, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(1, args.getTestLocation().length);
    }

    @Test
    public void setMultipleLocation(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.srcLocations = new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "g\\h\\i"));
            $.testLocations = new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "g\\h\\i"));
        }).build();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(3, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(3, args.getTestLocation().length);
    }

    @Test
    public void setMultipleSimilarLocation(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.srcLocations = new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "d\\e\\f", "g\\h\\i", "a\\b\\c"));
            $.testLocations = new HashSet<>(Arrays.asList("a\\b\\c", "d\\e\\f", "d\\e\\f", "g\\h\\i", "a\\b\\c"));
        }).build();

        Assert.assertNotNull(args.getSrcLocation());
        Assert.assertEquals(3, args.getSrcLocation().length);
        Assert.assertNotNull(args.getTestLocation());
        Assert.assertEquals(3, args.getTestLocation().length);
    }

    @Test
    public void setSingleOutputFile(){
        Arguments args = new ArgumentsBuilder().with($ -> {
            $.fromChangeId = "1";
            $.toChangeId = "2";
            $.repository = ".";
            $.outputFormat = "csv";
        }).build();

        Assert.assertEquals("csv", args.getOutputFormat());
    }
}
