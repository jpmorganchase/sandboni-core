package com.sandboni.core.engine.services;

import com.sandboni.core.engine.exception.GatewayException;

public interface ServerGateway {

    <T> void sendStats(T payload, String url) throws GatewayException;
}
