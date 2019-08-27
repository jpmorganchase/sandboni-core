package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;

public interface FileRenderer <R, T>{

    T renderBody(R resultObj, FileOptions fileOptions) throws RendererException;
}
