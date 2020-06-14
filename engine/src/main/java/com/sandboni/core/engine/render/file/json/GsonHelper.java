package com.sandboni.core.engine.render.file.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sandboni.core.engine.render.file.FormatHelper;

import java.lang.reflect.Type;

class GsonHelper implements FormatHelper {

    private Gson gson;

    public GsonHelper() {
        this.gson = new Gson();
    }

    @Override
    public <T> String marshal(T obj) {
        Type type = new TypeToken<T>(){}.getType();
        return gson.toJson(obj, type);
    }

}
