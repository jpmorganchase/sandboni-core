package com.sandboni.core.engine.render.file.properties;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.AbstractFileStrategy;
import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileRenderer;

import java.util.Map;

public class PropertiesFileStrategy <R extends Map> extends AbstractFileStrategy<R, R> {

    public PropertiesFileStrategy(R resultObj, FileRenderer<R, R> fileRenderer, FileOptions fileOptions) {
        super(resultObj, fileRenderer, fileOptions);
    }

    @Override
    public R render() throws RendererException {
        return fileRenderer.renderBody(resultObj, fileOptions);
    }
}
