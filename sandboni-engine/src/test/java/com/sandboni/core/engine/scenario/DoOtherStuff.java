package com.sandboni.core.engine.scenario;

public interface DoOtherStuff extends DoStuffBase, DoStuffSuper {

    default void doStuffViaInterface() {
    }

    default void doStuffDefault() {
    }
}
