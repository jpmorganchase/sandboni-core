package com.sandboni.core.engine.render.file.json;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.FileRenderer;
import com.sandboni.core.engine.render.file.json.util.GsonHelper;
import com.sandboni.core.engine.render.file.FormatHelper;

import java.util.Collection;

public class JSONFileRenderer<R extends Collection> implements FileRenderer<R, String> {

    protected FormatHelper jsonHelper;

    public JSONFileRenderer(){
        super();
        this.jsonHelper = new GsonHelper();
    }

    protected String parseResult(Object res) throws RendererException {
        return jsonHelper.marshal(res);
    }

    @Override
    public String renderBody(R resultObj, FileOptions fileOptions) throws RendererException {
        return parseResult(resultObj);
    }
}