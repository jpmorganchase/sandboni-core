package com.sandboni.core.scm.exception;

public class SourceControlRuntimeException extends RuntimeException {
    public SourceControlRuntimeException(Exception ex) {
        super(ex);
    }

    public SourceControlRuntimeException(String msg, Exception e) {
        super(msg, e);
    }
}
