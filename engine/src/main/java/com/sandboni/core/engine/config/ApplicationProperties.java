package com.sandboni.core.engine.config;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class ApplicationProperties {
    private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);
    public static final String PROP_PATH_KEY = "applicationProps";
    private static final String DEFAULT_PROP_PATH = "/application.properties";
    private final Map<ApplicationProperty, String> configMap = new EnumMap<>(ApplicationProperty.class);
    private static Supplier<ApplicationProperties> configSupplier = new CachingSupplier<>(ApplicationProperties::new);

    public String get(ApplicationProperty applicationProperty) {
        return configMap.get(applicationProperty);
    }

    public static ApplicationProperties getInstance() {
        return configSupplier.get();
    }

    //to help with test cases
    static synchronized void clearInstance() {
        configSupplier = new CachingSupplier<>(ApplicationProperties::new);
    }

    private ApplicationProperties() {
       init();
    }

    private void init() {
        try {
            String propertyFilePath = System.getProperty(PROP_PATH_KEY, DEFAULT_PROP_PATH);
            Properties fileProps = new Properties();
            fileProps.load(this.getClass().getResourceAsStream(propertyFilePath));

            //load props from file
            Arrays.stream(ApplicationProperty.values()).forEach(p -> configMap.put(p, fileProps.getProperty(p.toString())));

            //override props from file with those from System
            Arrays.stream(ApplicationProperty.values()).forEach(p -> {
                String property = System.getProperty(p.toString());
                if(property != null) {
                    configMap.put(p, property);
                }
            });
            log.info("Loaded props: {}", configMap);
        } catch (Exception e) {
            throw new ParseRuntimeException(e);
        }
    }
}
