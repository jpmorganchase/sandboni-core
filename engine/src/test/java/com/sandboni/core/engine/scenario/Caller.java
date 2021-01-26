package com.sandboni.core.engine.scenario;

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
        //list.stream().filter(c -> "empty".equals(c.toString())).count();
    }

    @SuppressWarnings("ReturnValueIgnored")
    public void referenceInLambdaSimple() {
        Arrays.stream(new String[]{"empty"})
                .filter(s -> s.equals(callee.toString()))
                .count();
    }

    @SuppressWarnings("ReturnValueIgnored")
    public void referenceInLambdaWithClass() {
        Predicate<String> test = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.equals(callee.toString());
            }
        };
        Arrays.stream(new String[]{"empty"})
                .filter(test)
                .count();
    }

    @SuppressWarnings("ReturnValueIgnored")
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

    public void testExecutorServiceExecute(){
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.execute(callee);
    }

    public void testExecutorServiceSubmit(){
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(callee);
    }


}