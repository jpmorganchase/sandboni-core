package com.sandboni.core.engine.exception;

public class GatewayException extends Exception {

    public GatewayException(String msg, Exception e) {
        super(msg, e);
    }
}
