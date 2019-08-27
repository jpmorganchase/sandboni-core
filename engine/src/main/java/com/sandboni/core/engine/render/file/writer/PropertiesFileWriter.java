package com.sandboni.core.engine.render.file.writer;

import com.sandboni.core.engine.exception.RendererException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileWriter implements FileWriter<Map<String, String>>{

    private static final Logger log = LoggerFactory.getLogger(PropertiesFileWriter.class);

    @Override
    public void write(Path filePath, Map<String, String> content) throws RendererException {
        try (OutputStream output = new FileOutputStream(filePath.toString())) {
            Properties prop = new Properties();
            prop.putAll(content);

            log.debug("the props to be stored {}", prop.stringPropertyNames());
            // storing properties to project root folder
            prop.store(output, null);

        } catch (Exception e) {
            throw new RendererException("Error when writing to properties file: " + content, e);
        }
    }
}
