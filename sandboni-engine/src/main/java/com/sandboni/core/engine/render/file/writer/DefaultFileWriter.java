package com.sandboni.core.engine.render.file.writer;

import com.sandboni.core.engine.exception.RendererException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFileWriter implements FileWriter<String> {

    @Override
    public void write(Path filePath, String content) throws RendererException {
        try {
            Files.write(filePath, content.getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new RendererException("Failed to write content to the file" , e);
        }
    }
}
