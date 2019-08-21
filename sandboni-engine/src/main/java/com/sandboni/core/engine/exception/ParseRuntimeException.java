package com.sandboni.core.engine.exception;

public class ParseRuntimeException extends RuntimeException {
    public ParseRuntimeException(Exception ex) {
        super(ex);
    }
}