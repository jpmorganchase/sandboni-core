package com.sandboni.core.engine.sta.connector;

import com.sandboni.core.engine.sta.Context;

public interface Connector {
    void connect(Context context);

    boolean proceed(Context context);
}
