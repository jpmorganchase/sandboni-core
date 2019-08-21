package com.sandboni.core.engine.render.file.json;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.AbstractFileStrategy;
import com.sandboni.core.engine.render.file.FileOptions;

import java.util.Collection;

public class JSONFileStrategy<R extends Collection> extends AbstractFileStrategy<R, String> {

    public JSONFileStrategy(R resultObj, JSONFileRenderer <R> fileRenderer, FileOptions fileOptions) {
       super(resultObj, fileRenderer, fileOptions);
    }

    @Override
    public String render() throws RendererException {
        return fileRenderer.renderBody(resultObj, fileOptions);
    }
}