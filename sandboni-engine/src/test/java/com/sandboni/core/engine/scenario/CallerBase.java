package com.sandboni.core.engine.scenario;

public class CallerBase {

    private int valueBase;

    public void doSuperStuff() {
        this.valueBase++;
    }

    public void doSuperStuffCaller() {
        doSuperStuff();
    }
}
