package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;


public interface FormatHelper {

    <T> String marshal(T modelClassObject) throws RendererException;

}
