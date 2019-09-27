package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;

import java.util.Collection;

public abstract class AbstractLangFileRenderer <R extends Collection> implements FileRenderer<R, String> {

    protected StringBuilder builder;

    public AbstractLangFileRenderer(){
        this.builder = new StringBuilder();
    }

    @Override
    public String renderBody(R resultObj, FileOptions fileOptions) throws RendererException {

        for (Object r : resultObj) {
            builder.append(parseResult(r)).append("\n");
        }
        return builder.toString();
    }

    protected abstract String parseResult(Object res) throws RendererException;

}
