package com.sandboni.core.engine.render.file;


public abstract class AbstractFileStrategy<R, T> implements FileStrategy<T> {

    protected final FileRenderer<R, T> fileRenderer;
    protected final R resultObj;
    protected final FileOptions fileOptions;

    public AbstractFileStrategy(R resultObj, FileRenderer<R, T> fileRenderer, FileOptions fileOptions) {
        this.fileRenderer = fileRenderer;
        this.resultObj = resultObj;
        this.fileOptions = fileOptions;
    }
}
