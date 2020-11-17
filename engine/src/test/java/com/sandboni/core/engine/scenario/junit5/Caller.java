package com.sandboni.core.engine.scenario.junit5;

import com.sandboni.core.engine.scenario.CallerBase;
import com.sandboni.core.engine.scenario.DoOtherStuff;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class Caller extends CallerBase {

    private static int valueStatic = -1;
    static {
        Caller.valueStatic = Callee.valueStatic;
    }
    private final Callee callee;
    private long value;

    Caller() {
        callee = new Callee();
    }

    public static void explicitStaticCall() {
        Callee.doStuffStatic();
    }

    void explicitCall() {
        callee.doStuff();
    }

    public void interfaceCall(DoOtherStuff doOtherStuff) {
        doOtherStuff.doStuffViaInterface();
    }

    public void interfaceDefaultCall(DoOtherStuff doOtherStuff) {
        doOtherStuff.doStuffDefault();
    }

    public void recursiveCall(int i) {
        if (i > 0) {
            recursiveCall(i - 1);
        }
    }

    public void instanceFieldReferenceGet() {
        this.value = callee.value;
    }

    public void staticFieldReferenceGet() {
        this.value = Callee.valueStatic;
    }

    public void instanceFieldReferencePut() {
        callee.value = (int) this.value;
    }

    public void staticFieldReferencePut() {
        Callee.valueStatic = (int) this.value;
    }

    public void constReference() throws Exception { // needs support
        if (!"test".equals(Callee.valueConst))
            throw new Exception("Unexpected");
    }

    public void genericReference(List<Callee> list) {
        this.value = list.get(0).toString().length();
    }

    public void referenceInLambdaSimple() {
        Arrays.stream(new String[]{"empty"})
                .filter(s -> s.equals(callee.toString()))
                .count();
    }

    public void referenceInLambdaWithClass() {
        Predicate<String> test = s -> s.equals(callee.toString());
        Arrays.stream(new String[]{"empty"})
                .filter(test)
                .count();
    }

    public void referenceInLambdaWithMethodHandle() {
        Arrays.stream(new String[]{"empty"})
                .filter(callee::filter)
                .count();
    }

    @Override
    public void doSuperStuff() {
        super.doSuperStuff();
    }

    public void threadRunReference() {
        Thread thread = new Thread(callee);
        thread.start();
    }

    public void threadRunReferenceLambda() {
        Thread thread = new Thread(() -> callee.run());
        thread.start();
    }

    public void callToFinalizeMethod(){
        Callee c = new Callee();
    }

    public void callToMethodA() {
        callee.methodA();
    }

    public void testExecutorServiceExecute(){
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.execute(callee);
    }

    public void testExecutorServiceSubmit(){
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(callee);
    }


}