package com.sandboni.core.engine.scenario;

import org.junit.*;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

@Ignore("This is a test for actual tests to discover")
public class PlainTest {

    private final Caller c;

    public PlainTest() {
        c = new Caller();
    }

    @Test
    public void testExplicitCall() {
        c.explicitCall();
        assertTrue(true);
    }

    @Test
    @Ignore
    @SuppressWarnings("squid:S1607")
    public void testIgnoredCall(){
        c.explicitCall();
        assertTrue(true);
    }

    @Test
    public void testExplicitStaticCall() {
        Caller.explicitStaticCall();
        assertTrue(true);
    }

    @Test
    public void testInterfaceCall() {
        c.interfaceCall(new Callee());
        assertTrue(true);
    }

    @Test
    public void testInterfaceDefaultCall() {
        c.interfaceDefaultCall(new Callee());
        assertTrue(true);
    }

    @Test
    public void testSuperCallingVirtualChild() {
        c.doSuperStuffCaller();
        assertTrue(true);
    }

    @Test
    public void testSuper() {
        c.doSuperStuff();
        assertTrue(true);
    }

    @Test
    public void testRecursion() {
        c.recursiveCall(10);
        assertTrue(true);
    }

    @Test
    public void testInstanceFieldReferenceGet() {
        c.instanceFieldReferenceGet();
        assertTrue(true);
    }

    @Test
    public void testStaticFieldReferenceGet() {
        c.staticFieldReferenceGet();
        assertTrue(true);
    }

    @Test
    public void testInstanceFieldReferencePut() {
        c.instanceFieldReferencePut();
        assertTrue(true);
    }

    @Test
    public void testStaticFieldReferencePut() {
        c.staticFieldReferencePut();
        assertTrue(true);
    }

    @Test
    public void testConstReference() throws Exception {
        c.constReference();
        assertTrue(true);
    }

    @Test
    public void testGenericReference() {
        c.genericReference(Collections.singletonList(new Callee()));
        assertTrue(true);
    }

    @Test
    public void testReferenceInLambdaSimple() {
        c.referenceInLambdaSimple();
        assertTrue(true);
    }

    @Test
    public void testReferenceInLambdaWithClass() {
        c.referenceInLambdaWithClass();
        assertTrue(true);
    }

    @Test
    public void testReferenceInLambdaWithMethodHandle() {
        c.referenceInLambdaWithMethodHandle();
        assertTrue(true);
    }

    @Test
    public void testThreadRunReference() {
        c.threadRunReference();
        assertTrue(true);
    }

    @Test
    public void testThreadRunReferenceLambda() {
        c.threadRunReferenceLambda();
        assertTrue(true);
    }

    @Test
    public void testDisconnectedTest() {
        //doing nothing.
        assertTrue(true);
    }

    @Before
    public void before() {
        //doing nothing.
    }

    @BeforeClass
    public static void beforeClass() {
        //doing nothing.
    }

    @After
    public void after() {
        //doing nothing.
    }

    @AfterClass
    public static void afterClass() {
        //doing nothing.
    }
}