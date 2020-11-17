package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.PoCDiffChangeDetector;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import com.sandboni.core.engine.finder.bcel.visitors.MethodUtils;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sandboni.core.engine.MockChangeDetector.*;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.CUCUMBER_RUNNER_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class BcelFinderTest extends FinderTestBase {
    private static final String CALLER_ACTOR_VERTEX = PACKAGE_NAME + ".Caller";
    private final String PLAIN_JUNIT5_TEST_ACTOR_VERTEX = PACKAGE_NAME + "." + JUNIT5 + ".PlainJUnit5Test";
    private final String CALLEE_ACTOR_VERTEX = PACKAGE_NAME + ".Callee";
    private final String DO_OTHER_STUFF_ACTOR_VERTEX = PACKAGE_NAME + ".DoOtherStuff";
    private final String PLAIN_TEST_ACTOR_VERTEX = PACKAGE_NAME + ".PlainTest";
    private final String EMPTY_TEST_ACTOR_VERTEX = PACKAGE_NAME + ".EmptyTest";
    private final String HTTP_TEST_ACTOR_VERTEX = PACKAGE_NAME + ".HttpTest";

    private void testVisitor(Link[] expectedLinks) {
        Finder f = new BcelFinder();
        f.findSafe(context);

        assertLinksExist(expectedLinks);
    }

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testCallerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testSpringControllerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testJavaxControllerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testTestClassVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testInheritanceVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testAffectedVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    private void testImplementingVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks);
    }

    @Test
    public void testExplicitCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitCall()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "doStuff()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testExplicitStaticCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitStaticCall()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "doStuffStatic()").build(),
                LinkType.STATIC_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "interfaceCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(DO_OTHER_STUFF_ACTOR_VERTEX, "doStuffViaInterface()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testDefaultInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "interfaceDefaultCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(DO_OTHER_STUFF_ACTOR_VERTEX, "doStuffDefault()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testThreadRunCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "threadRunReference()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceImpl() {
        Link expectedLink1 = newLink(new Vertex.Builder(DO_OTHER_STUFF_ACTOR_VERTEX, "doStuffViaInterface()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "doStuffViaInterface()").build(), LinkType.INTERFACE_IMPL);

        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoStuffBase", "doStuffViaInterface()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "doStuffViaInterface()").build(),
                LinkType.INTERFACE_IMPL);
        testImplementingVisitor(expectedLink1, expectedLink2);
    }

    @Test
    public void testGenericReference() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "genericReference(java.util.List)").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "instanceFieldReferenceGet()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "value").build(),
                LinkType.FIELD_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "instanceFieldReferencePut()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "value").build(),
                LinkType.FIELD_PUT);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "staticFieldReferenceGet()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "valueStatic").build(),
                LinkType.STATIC_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "staticFieldReferencePut()").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "valueStatic").build(),
                LinkType.STATIC_PUT);
        testCallerVisitor(expectedLink);
    }


    @Test
    public void testImplicitExecutorServiceExecuteLink(){
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "testExecutorServiceExecute()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testImplicitExecutorServiceSubmitLink(){
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "testExecutorServiceSubmit()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testOnlyMethodAnnotatedWithPath(){
        Link expectedLink = newLink(new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testJavaxOnlyMethodAnnotated()").build(),
                new Vertex.Builder("TRACE http://localhost/*", "/basic-quote-requests/annotation-only-method")
                        .markSpecial()
                        .build()
                ,LinkType.HTTP_REQUEST);
        testJavaxControllerVisitor(expectedLink);
    }

    @Test
    public void testTestMethodDetection() {
        TestVertex tv = new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()",null).withIgnore(true).build();
        Link expectedLink = newLink(START_VERTEX, tv, LinkType.ENTRY_POINT);
        TestVertex tvJunit5 = new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()",null).withIgnore(true).build();
        Link expectedJUnit5Link = newLink(START_VERTEX, tvJunit5, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink, expectedJUnit5Link);
    }

    @Test
    public void testIgnoredMethodDetection() {
        TestVertex tv = new TestVertex.Builder(EMPTY_TEST_ACTOR_VERTEX, "testIgnoredMethod()", null).withIgnore(true).build();
        Link expectedLink = newLink(START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testNotIgnoredMethodDetection() {
        TestVertex tv = new TestVertex.Builder(EMPTY_TEST_ACTOR_VERTEX, "testNotIgnoredMethod()", null).build();
        Link expectedLink = newLink(START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testTestIgnoredMethodAndClassDetection() {
        TestVertex tv = new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testIgnoredCall()", null).withIgnore(true).build();
        Link expectedLink = newLink(START_VERTEX, tv, LinkType.ENTRY_POINT);
        TestVertex tvJunit5 = new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testIgnoredCall()", null).withIgnore(true).build();
        Link expectedLinkJunit5 = newLink(START_VERTEX, tvJunit5, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink, expectedLinkJunit5);
    }

    @Test
    public void testConstructorCallDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()",null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, MethodUtils.INIT).build(),
                LinkType.METHOD_CALL);
        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()",null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, MethodUtils.INIT).build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }
    @Test
    public void testStaticConstructorCallDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, MethodUtils.CLINIT).build(),
                LinkType.STATIC_CALL);

        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, MethodUtils.CLINIT).build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }

    @Test
    public void testStaticNestedTestCallDetection() {
        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX + "$ATest", "testMethodAFromNested()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX  + "$ATest" , MethodUtils.CLINIT).build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLinkJUnit5);
    }

    @Test
    public void testBeforeMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "before()").build(),
                LinkType.METHOD_CALL);
        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "before()").build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }

    @Test
    public void testAfterMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "after()").build(),
                LinkType.METHOD_CALL);
        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "after()").build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }

    @Test
    public void testBeforeClassMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "beforeClass()").build(),
                LinkType.STATIC_CALL);
        Link expectedLinkJUnit5 = newLink(new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "beforeClass()").build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }

    @Test
    public void testAfterClassMethodsDetection() {
        Link expectedLink = newLink(
                new TestVertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_TEST_ACTOR_VERTEX, "afterClass()").build(),
                LinkType.STATIC_CALL);
        Link expectedLinkJUnit5 = newLink(
                new TestVertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "testExplicitCall()", null).build(),
                new Vertex.Builder(PLAIN_JUNIT5_TEST_ACTOR_VERTEX, "afterClass()").build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink, expectedLinkJUnit5);
    }
    @Test
    public void testOverriddenMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuff()").build(),
                new Vertex.Builder(CALLER_ACTOR_VERTEX, "doSuperStuff()").build()
                , LinkType.OVERRIDDEN);
        testInheritanceVisitor(expectedLink);
    }

    @Test
    public void testInheritedMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "doSuperStuffCaller()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuffCaller()").build(),
                LinkType.FORWARD_TO);
        testInheritanceVisitor(expectedLink);
    }

    @Test
    public void testInheritedInterfaceMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(DO_OTHER_STUFF_ACTOR_VERTEX, "doStuffViaInterfaceSuper()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".DoStuffSuper", "doStuffViaInterfaceSuper()").build(),
                LinkType.FORWARD_TO);
        testInheritanceVisitor(expectedLink);
    }


    private Vertex getGetHttpVertex(String method, String action) {
        return new Vertex.Builder(method + " " + HttpConsts.HTTP_LOCALHOST, action)
                .markSpecial()
                .build();
    }

    @Test
    public void testHttpCallTestMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testHttpVerbCall()").build(),
                getGetHttpVertex("GET", "/scenario/explicitCall"),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationMapDetection() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testDisconnectedSpringAnnotationMap()").build(),
                new Vertex.Builder("GET" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionGet() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("GET" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPost() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("POST" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPatch() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("PATCH" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPut() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("PUT" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionDelete() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("DELETE" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionTrace() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("TRACE" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testJavaxAnnotationMapDetection() {
        Link expectedLink = newLink(
                new Vertex.Builder(HTTP_TEST_ACTOR_VERTEX, "testDisconnectedJavaxAnnotationMap()").build(),
                new Vertex.Builder("PUT" + " " + HttpConsts.HTTP_LOCALHOST, "/basic-quote-requests/annotation").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerMethodDetection() {
        Link expectedLink = newLink(getGetHttpVertex("GET", "/scenario/explicitCall"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "explicitCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionGet() {
        Link expectedLink = newLink(getGetHttpVertex("GET", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionPost() {
        Link expectedLink = newLink(getGetHttpVertex("POST", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionPut() {
        Link expectedLink = newLink(getGetHttpVertex("PUT", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionPatch() {
        Link expectedLink = newLink(getGetHttpVertex("PATCH", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionDelete() {
        Link expectedLink = newLink(getGetHttpVertex("DELETE", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerNoMethodDetectionTrace() {
        Link expectedLink = newLink(getGetHttpVertex("TRACE", "/scenario/annotation-no-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationNoMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerGetAnnotationDetection() {
        Link expectedLink = newLink(getGetHttpVertex("GET", "/scenario/annotation-get-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationGetMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerPostAnnotationDetection() {
        Link expectedLink = newLink(getGetHttpVertex("POST", "/scenario/annotation-post-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationPostMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerPutAnnotationDetection() {
        Link expectedLink = newLink(getGetHttpVertex("PUT", "/scenario/annotation-put-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationPutMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerDeleteAnnotationDetection() {
        Link expectedLink = newLink(getGetHttpVertex("DELETE", "/scenario/annotation-delete-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationDeleteMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    @Test
    public void testSpringHttpControllerPatchAnnotationDetection() {
        Link expectedLink = newLink(getGetHttpVertex("PATCH", "/scenario/annotation-patch-method"),
                new Vertex.Builder(PACKAGE_NAME + ".SpringController", "annotationPatchMethodMapCall()").build(),
                LinkType.HTTP_HANLDER);
        testSpringControllerVisitor(expectedLink);
    }

    private Vertex getPostHttpVertex() {
        return new Vertex.Builder("POST" + " " + HttpConsts.HTTP_LOCALHOST, "/basic-quote-requests/req").markSpecial().build();
    }

    private Vertex getGetClashHttpVertex() {
        return new Vertex.Builder("GET" + " " + HttpConsts.HTTP_LOCALHOST, "/basic-quote-requests/").markSpecial().build();
    }

    @Test
    public void testJavaxHttpControllerMethodDetection() {
        Link expectedLink = newLink(getPostHttpVertex(),
                new Vertex.Builder(PACKAGE_NAME + ".JavaxController", "submitRequest(java.lang.String)").build(),
                LinkType.HTTP_HANLDER);
        testJavaxControllerVisitor(expectedLink);
    }

    @Test
    public void testJavaxHttpControllerTemplateClashDetection() {
        Link expectedLink = newLink(getGetClashHttpVertex(),
                new Vertex.Builder(PACKAGE_NAME + ".JavaxController", "getAll(java.lang.String)").build(),
                LinkType.HTTP_HANLDER);
        testJavaxControllerVisitor(expectedLink);
    }

    @Test
    public void testAffectedMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuff()").build(), END_VERTEX, LinkType.EXIT_POINT);
        testAffectedVisitor(expectedLink);
    }

    @Test
    public void testReferenceInLambdaDetection() {
        Link expectedLink1 = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "referenceInLambdaSimple()").build(),
                new Vertex.Builder(CALLER_ACTOR_VERTEX, "lambda$referenceInLambdaSimple$0(java.lang.String)").build(),
                LinkType.DYNAMIC_CALL);
        Link expectedLink2 = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "lambda$referenceInLambdaSimple$0(java.lang.String)").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink1, expectedLink2);
    }

    @Test
    public void testReferenceInLambdaWithClass() {
        Link expectedLink1 = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "referenceInLambdaWithClass()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller$1", "test(java.lang.String)").build(),
                LinkType.METHOD_CALL);

        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller$1", "test(java.lang.String)").build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink1, expectedLink2);
    }

    @Ignore("This will fail because we do not have enough info for metadata (i.e. variable) changes")
    @Test
    public void testAffectedFieldDetection() {
        Link expectedLink = newLink(new Vertex.Builder(CALLEE_ACTOR_VERTEX, "value").build(), END_VERTEX, LinkType.EXIT_POINT);
        testAffectedVisitor(expectedLink);
    }

    @Test(expected = ParseRuntimeException.class)
    public void testInvalidContextLocation() {

        Context context = new Context(new String[]{"non-existing-location"}, new String[]{"non-existing-location"}, "com.sandboni", new ChangeScopeImpl(), null);
        Finder f = new BcelFinder();
        f.findSafe(context);
    }

    @Test
    public void testPoCDiffChangeDetector() {
        PoCDiffChangeDetector detector = new PoCDiffChangeDetector();
        ChangeScope<Change> changes = detector.getChanges("1", "2");
        Assert.assertFalse(changes.getAllAffectedClasses().isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testNoConstructorCall() {
        Link expectedLink = newLink(new Vertex.Builder( PACKAGE_NAME + ".BasicScenario", MethodUtils.INIT).build(),
                new Vertex.Builder(CALLEE_ACTOR_VERTEX, MethodUtils.INIT).build(),
                LinkType.SPECIAL_CALL);
        testCallerVisitor(expectedLink);
    }


    @Test
    public void testMockMvcGetDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".SpringMockTest", "testMockGet()").build(),
                new Vertex.Builder("GET http://localhost/*", "/scenario/mockGet").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testMockMvcPostDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".SpringMockTest", "testMockPost()").build(),
                new Vertex.Builder("POST http://localhost/*", "/scenario/mockPost").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testMockMvcPutDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".SpringMockTest", "testMockPut()").build(),
                new Vertex.Builder("PUT http://localhost/*", "/scenario/mockPut").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testMockMvcPatchDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".SpringMockTest", "testMockPatch()").build(),
                new Vertex.Builder("PATCH http://localhost/*", "/scenario/mockPatch").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testMockMvcDeleteDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".SpringMockTest", "testMockDelete()").build(),
                new Vertex.Builder("DELETE http://localhost/*", "/scenario/mockDelete").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testIgnoredClass() {
        Link expectedLink = newLink(
                START_VERTEX,
                new TestVertex.Builder(PACKAGE_NAME + ".IgnoredClassTest", "testHttpVerbCall()", null).withIgnore(true).build(),
                LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testNotIgnoredPowerMockClass() {
        Link expectedLink = newLink(
                START_VERTEX,
                new TestVertex.Builder(PACKAGE_NAME + ".PowerMockIgnoreTest", "testSomeMethod()", null).withIgnore(false).build(),
                LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testTestAlwaysRunTest() {
        Link expectedLink1 = newLink(START_VERTEX, new TestVertex.Builder(PACKAGE_NAME + ".AlwaysRunClassTest", "testOne()",null).withAlwaysRun(true).build(), LinkType.ENTRY_POINT);
        Link expectedLink2 = newLink(START_VERTEX, new TestVertex.Builder(PACKAGE_NAME + ".AlwaysRunClassTest", "testTwo()",null).withAlwaysRun(true).build(), LinkType.ENTRY_POINT);

        Link expectedLink3 = newLink(START_VERTEX, new TestVertex.Builder(PACKAGE_NAME + ".AlwaysRunMethodTest", "testOne()",null).withAlwaysRun(false).build(), LinkType.ENTRY_POINT);
        Link expectedLink4 = newLink(START_VERTEX, new TestVertex.Builder(PACKAGE_NAME + ".AlwaysRunMethodTest", "testTwo()",null).withAlwaysRun(true).build(), LinkType.ENTRY_POINT);

        Link expectedLink5 = newLink(START_VERTEX, new TestVertex.Builder(PACKAGE_NAME + ".AlwaysRunCategoryClassTest", "testOne()",null).withAlwaysRun(true).build(), LinkType.ENTRY_POINT);

        testTestClassVisitor(expectedLink1, expectedLink2, expectedLink3, expectedLink4, expectedLink5);
    }

    @Test
    public void testCucumberRunnerDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".CucumberRunner", "runWith", TEST_LOCATION).build(),
                CUCUMBER_RUNNER_VERTEX,
                LinkType.CUCUMBER_RUNNER);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testInterfaceWithLambdas() {
        Link interfaceToImplementingClass = newLink(new Vertex.Builder("java.util.function.Function", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".DeltaOneBlotterConverter", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.INVOKE_VIRTUAL);

        Link interfaceGenericToSpecificMethod = newLink(new Vertex.Builder("java.util.function.Function", "apply(java.lang.Object)").build(),
                new Vertex.Builder("java.util.function.Function", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.INVOKE_VIRTUAL);
        testImplementingVisitor(interfaceToImplementingClass, interfaceGenericToSpecificMethod);
    }

    @Test
    public void testFoundUsingFunctionalInterfaceFramework() {
        Link interfaceToImplementingClass = newLink(new Vertex.Builder("java.util.function.Function", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".DeltaOneBlotterConverter", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.INVOKE_VIRTUAL);

        Link interfaceGenericToSpecificMethod = newLink(new Vertex.Builder("java.util.function.Function", "apply(java.lang.Object)").build(),
                new Vertex.Builder("java.util.function.Function", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.INVOKE_VIRTUAL);
        testImplementingVisitor(interfaceToImplementingClass, interfaceGenericToSpecificMethod);

        Link specificsInterfaceToFunction = newLink(new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".SpecificsConverter", "apply(java.lang.Object)").build(),
                new Vertex.Builder("java.util.function.Function", "apply(java.lang.Object)").build(),
                LinkType.FORWARD_TO);
        testInheritanceVisitor(specificsInterfaceToFunction);

        Link lambdaToInterface = newLink(new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".RecordConverter", String.format("lambda$apply$0(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".SpecificsConverter", "apply(java.lang.Object)").build(),
                LinkType.INTERFACE_CALL);
        Link interfaceToLambda = newLink(new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".RecordConverter", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".RecordConverter", String.format("lambda$apply$0(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.DYNAMIC_CALL);
        Link testToInterfaceCall = newLink(new TestVertex.Builder(LAMBDA_PACKAGE_TEST_NAME + ".DeltaOneBlotterConverterTest", "applyTests()").build(),
                new Vertex.Builder(LAMBDA_PACKAGE_NAME + ".RecordConverter", String.format("apply(%s.BlotterInput)", LAMBDA_PACKAGE_NAME)).build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(lambdaToInterface, interfaceToLambda, testToInterfaceCall);
    }

    // This test must pass when Sandboni is compiled with Java 11
    @Ignore
    @Test
    public void testStringConcatenation() {
        Link stringConcat = newLink(new Vertex.Builder("com.sandboni.core.engine.scenario.string.StringConcatenation", "basicStringConcatenation()").build(),
                new Vertex.Builder("java.lang.invoke.StringConcatFactory", "makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.String,java.lang.Object[])").build(),
                LinkType.DYNAMIC_CALL);

        testImplementingVisitor(stringConcat);
    }
}