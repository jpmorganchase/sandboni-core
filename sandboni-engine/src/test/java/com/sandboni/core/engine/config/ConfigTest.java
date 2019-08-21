package com.sandboni.core.engine.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigTest {

    @Before
    public void init() {
        System.setProperty(Config.PROP_PATH_KEY, "/test.properties");
        Arrays.stream(ConfigProp.values()).forEach(p -> System.clearProperty(p.toString()));
        Config.clearInstance();
    }

    @Test
    public void getInstanceTest() {
        Config config = Config.getInstance();

        Arrays.stream(ConfigProp.values()).forEach(p -> assertNotNull(config.get(p)));
        assertEquals("val2", config.get(ConfigProp.SERVER_BASE_URL));
        assertEquals("val3", config.get(ConfigProp.SAVE_COVERAGE));
    }

    @Test
    public void getInstanceTestSystemOverride() {
        System.setProperty(ConfigProp.SERVER_BASE_URL.toString(), "val98");

        Config config = Config.getInstance();

        Arrays.stream(ConfigProp.values()).forEach(p -> assertNotNull(config.get(p)));
        assertEquals("val98", config.get(ConfigProp.SERVER_BASE_URL));
        assertEquals("val3", config.get(ConfigProp.SAVE_COVERAGE));
    }

    @Test(expected = Exception.class)
    public void getInstanceNoFile() {
        System.setProperty(Config.PROP_PATH_KEY, "/test123.properties");
        Config.getInstance();
    }
}
