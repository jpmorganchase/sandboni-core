package com.sandboni.core.engine.render.file.xml;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.AbstractLangFileRenderer;
import com.sandboni.core.engine.render.file.FormatHelper;

import java.util.Collection;

public class XMLFileRenderer<R extends Collection> extends AbstractLangFileRenderer<R> {

    protected FormatHelper xmlHelper;

    public XMLFileRenderer() {
        super();
        this.xmlHelper = new JaxbHelper();
    }

    protected String parseResult(Object res) throws RendererException {
        return xmlHelper.marshal(res);
    }
}