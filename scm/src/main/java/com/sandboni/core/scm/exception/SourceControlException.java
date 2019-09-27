package com.sandboni.core.scm.exception;

public class SourceControlException extends Exception {
    public SourceControlException(String msg, Exception e) {
        super(msg, e);
    }

    public SourceControlException(String msg) {
        super(msg);
    }
}
