package com.sandboni.core.engine.scenario;

public class BasicScenario {


    private Callee callee = new Callee();

    public BasicScenario(){
        System.out.println("in");
        //do nothing
    }

    public void printCallee(){
        System.out.println(callee);
    }
}
