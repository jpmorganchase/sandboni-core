package com.sandboni.core.engine.result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Result {

    private final Map<ResultContent, Map<Class<?>, Object >> map;

    private FilterIndicator filterIndicator;

    private Status status;

    public Result(){
        this.map = new ConcurrentHashMap<>();
    }

    public <T> void put(ResultContent rc, Class<T> type, T content) {
        map.computeIfAbsent(rc, r -> new HashMap<>()).put(type, content);
    }

    public <T> T get(ResultContent rc){
        return getActualResult(rc);
    }

    public <T> T get() {
        if (map.size() != 1)
            throw new IllegalArgumentException("Result doesn't contain single value, use get(ResultContent) or get(ResultContent, Clazz) instead");

        ResultContent rc = map.keySet().iterator().next();
        return getActualResult(rc);
    }

    private <T> T getActualResult(ResultContent rc) {
        Class<T> clazz = (Class<T>) map.get(rc).keySet().iterator().next();
        return get(rc, clazz);
    }

    private <T> T get(ResultContent rc, Class<T> key){
        Object untypedData =  map.get(rc).get(key);
        return key.cast(untypedData);
    }

    public boolean isSuccess(){
        return Status.OK.equals(this.status);
    }

    public FilterIndicator getFilterIndicator() {
        return filterIndicator;
    }

    public void setFilterIndicator(FilterIndicator filterIndicator) {
        this.filterIndicator = filterIndicator;
    }

    public boolean isError(){
        return Status.ERROR.equals(this.status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}