package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;

public interface FileStrategy<T> {

    T render() throws RendererException;

}
