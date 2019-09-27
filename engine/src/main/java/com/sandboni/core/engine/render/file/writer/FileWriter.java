package com.sandboni.core.engine.render.file.writer;

import com.sandboni.core.engine.exception.RendererException;

import java.nio.file.Path;

public interface FileWriter<R> {

    void write(Path filePath, R content) throws RendererException;
}
