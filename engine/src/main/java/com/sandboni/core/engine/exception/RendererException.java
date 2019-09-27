package com.sandboni.core.engine.exception;

public class RendererException extends Exception {

    public RendererException(String msg) {
        super(msg);
    }

    public RendererException(String msg, Exception e) {
        super(msg, e);
    }
}
