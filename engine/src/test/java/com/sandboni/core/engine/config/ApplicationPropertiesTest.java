package com.sandboni.core.engine.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.sandboni.core.engine.config.ApplicationProperties.PROP_PATH_KEY;
import static com.sandboni.core.engine.config.ApplicationProperty.SAVE_COVERAGE;
import static com.sandboni.core.engine.config.ApplicationProperty.SERVER_BASE_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationPropertiesTest {

    @Before
    public void init() {
        System.setProperty(PROP_PATH_KEY, "/test.properties");
        Arrays.stream(ApplicationProperty.values()).forEach(p -> System.clearProperty(p.toString()));
        ApplicationProperties.clearInstance();
    }

    @Test
    public void getInstanceTest() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

        Arrays.stream(ApplicationProperty.values()).forEach(p -> assertNotNull(applicationProperties.get(p)));
        assertEquals("val2", applicationProperties.get(SERVER_BASE_URL));
        assertEquals("val3", applicationProperties.get(SAVE_COVERAGE));
    }

    @Test
    public void getInstanceTestSystemOverride() {
        System.setProperty(SERVER_BASE_URL.toString(), "val98");

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

        Arrays.stream(ApplicationProperty.values()).forEach(p -> assertNotNull(applicationProperties.get(p)));
        assertEquals("val98", applicationProperties.get(SERVER_BASE_URL));
        assertEquals("val3", applicationProperties.get(SAVE_COVERAGE));
    }

    @Test(expected = Exception.class)
    public void getInstanceNoFile() {
        System.setProperty(PROP_PATH_KEY, "/test123.properties");
        ApplicationProperties.getInstance();
    }
}
