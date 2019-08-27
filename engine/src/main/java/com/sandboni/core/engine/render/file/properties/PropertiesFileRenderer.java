package com.sandboni.core.engine.render.file.properties;

import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileRenderer;

import java.util.Map;

public class PropertiesFileRenderer<R extends Map> implements FileRenderer<R, R> {

    @Override
    public R renderBody(R resultObj, FileOptions fileOptions) {
        return resultObj;
    }
}
