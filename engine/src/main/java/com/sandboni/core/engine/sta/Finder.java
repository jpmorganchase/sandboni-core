package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.exception.ParseRuntimeException;
import com.sandboni.core.engine.sta.Context;

import java.io.IOException;

public interface Finder {
    void find(Context context) throws IOException;

    default void findSafe(Context context){
        try{
            this.find(context);
        }
        catch (Exception e){
            throw new ParseRuntimeException(e);
        }
    }
}