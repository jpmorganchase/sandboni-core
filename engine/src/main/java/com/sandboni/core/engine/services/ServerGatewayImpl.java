package com.sandboni.core.engine.services;

import com.google.gson.Gson;
import com.sandboni.core.engine.exception.GatewayException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerGatewayImpl implements ServerGateway {
    private static final Logger log = LoggerFactory.getLogger(ServerGatewayImpl.class);

    @Override
    public <T> void sendStats(T payload, String url) throws GatewayException {
        try {
            HttpEntity httpEntity = EntityBuilder.create()
                    .setContentType(ContentType.APPLICATION_JSON)
                    .setText(new Gson().toJson(payload))
                    .build();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(httpEntity);

            HttpClient httpClient = createHttpClient();
            httpClient.execute(httpPost);
            log.debug("Sending to {} data: {}", url, payload);
        } catch (Exception e) {
            throw new GatewayException("Exception sending to server", e);
        }
    }

    public HttpClient createHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
