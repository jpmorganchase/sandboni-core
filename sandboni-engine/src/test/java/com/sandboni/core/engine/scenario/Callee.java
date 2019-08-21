package com.sandboni.core.engine.scenario;


public class Callee implements DoOtherStuff, Runnable {

    public static final String valueConst = "test";
    public static int valueStatic = 0;
    public int value = 1;
    public int uninitialized;
    public String value2 = new String(
            "12344" +
                    // comment
                    "2345");

    public Callee() {
    }

    public Callee(int value) {
        this.value = value + 1;
    }

    public static void doStuffStatic() {
        valueStatic++;
    }

    public void doStuff() {
        value++;
    }

    @Override
    public void doStuffViaInterface() {
        value--;
    }

    @Override
    public String toString() {
        return "emtpy";
    }

    @Override
    public void run() {
    }

    public void notReferenced() {
    }

    public boolean filter(String a) {
        return true;
    }

    @Override
    public void finalize(){
        int i =0;
    }
}