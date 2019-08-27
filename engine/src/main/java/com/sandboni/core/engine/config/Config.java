package com.sandboni.core.engine.config;

import com.sandboni.core.engine.common.CachingSupplier;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static final String PROP_PATH_KEY = "applicationProps";
    private static final String DEFAULT_PROP_PATH = "/application.properties";
    private final Map<ConfigProp, String> configMap = new EnumMap<>(ConfigProp.class);
    private static Supplier<Config> configSupplier = new CachingSupplier<>(Config::new);

    public String get(ConfigProp configProp) {
        return configMap.get(configProp);
    }

    public static Config getInstance() {
        return configSupplier.get();
    }

    //to help with test cases
    static synchronized void clearInstance() {
        configSupplier = new CachingSupplier<>(Config::new);
    }

    private Config() {
       init();
    }

    private void init() {
        try {
            String propertyFilePath = System.getProperty(PROP_PATH_KEY, DEFAULT_PROP_PATH);
            Properties fileProps = new Properties();
            fileProps.load(this.getClass().getResourceAsStream(propertyFilePath));

            //load props from file
            Arrays.stream(ConfigProp.values()).forEach(p -> configMap.put(p, fileProps.getProperty(p.toString())));

            //override props from file with those from System
            Arrays.stream(ConfigProp.values()).forEach(p -> {
                String property = System.getProperty(p.toString());
                if(property != null) {
                    configMap.put(p, property);
                }
            });
            LOGGER.info("Loaded props: {}", configMap);
        } catch (IOException e) {
            throw new ParseRuntimeException(e);
        }
    }
}
