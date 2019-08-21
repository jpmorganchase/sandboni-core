package com.sandboni.core.engine.render.file;

import org.junit.Assert;
import org.junit.Test;

public class FileOptionsTest {

    @Test
    public void testAttributes(){
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "myJsonTest.json";
                    fo.type = FileType.JSON;
                    fo.attributes = "{key1:val1, key2:val2}"; })
                .build();
        Assert.assertEquals(2, fileOptions.getAttributes().size());
    }

    @Test(expected = NullPointerException.class)
    public void testNoName(){
        new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.type = FileType.JSON;
                    fo.attributes = "{key1:val1, key2:val2}"; })
                .build();
    }

    @Test
    public void testName(){
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "myJsonTest.json";
                    fo.type = FileType.JSON;
                    fo.attributes = "{key1:val1, key2:val2}"; })
                .build();

        Assert.assertEquals(FileType.JSON, fileOptions.getType());
    }

    @Test(expected = NullPointerException.class)
    public void testNullType(){
        FileOptions fileOptions = new FileOptions.FileOptionsBuilder()
                .with(fo -> {
                    fo.name = "myJsonTest.json";
                    fo.attributes = "{key1:val1, key2:val2}"; })
                .build();

        Assert.assertEquals("myJsonTest.json", fileOptions.getName());
    }
}
