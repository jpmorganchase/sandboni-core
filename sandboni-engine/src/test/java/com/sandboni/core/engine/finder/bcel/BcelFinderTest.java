package com.sandboni.core.engine.finder.bcel;

import com.sandboni.core.engine.FinderTestBase;
import com.sandboni.core.engine.PoCDiffChangeDetector;
import com.sandboni.core.engine.contract.Finder;
import com.sandboni.core.engine.contract.HttpConsts;
import com.sandboni.core.engine.exception.ParseRuntimeException;
import com.sandboni.core.engine.finder.bcel.visitors.*;
import com.sandboni.core.engine.finder.bcel.visitors.http.JavaxControllerClassVisitor;
import com.sandboni.core.engine.finder.bcel.visitors.http.SpringControllerClassVisitor;
import com.sandboni.core.engine.sta.Context;
import com.sandboni.core.engine.sta.graph.Link;
import com.sandboni.core.engine.sta.graph.LinkType;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.scope.ChangeScopeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sandboni.core.engine.MockChangeDetector.PACKAGE_NAME;

public class BcelFinderTest extends FinderTestBase {
    private static final String CALLER_ACTOR_VERTEX = PACKAGE_NAME + ".Caller";

    private void testVisitor(Link[] expectedLinks, ClassVisitor... visitors) {
        Finder f = new BcelFinder(visitors);
        f.findSafe(context);

        assertLinksExist(expectedLinks);
    }

    @Before
    public void setUp() {
        super.initializeContext();
    }

    private void testCallerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new CallerClassVisitor());
    }

    private void testSpringControllerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new SpringControllerClassVisitor());
    }

    private void testJavaxControllerVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new JavaxControllerClassVisitor());
    }

    private void testTestClassVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new TestClassVisitor());
    }

    private void testInheritanceVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new InheritanceClassVisitor());
    }

    private void testAffectedVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new AffectedClassVisitor());
    }

    private void testImplementingVisitor(Link... expectedLinks) {
        testVisitor(expectedLinks, new ImplementingClassVisitor());
    }

    @Test
    public void testExplicitCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitCall()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuff()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testExplicitStaticCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "explicitStaticCall()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffStatic()").build(),
                LinkType.STATIC_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(CALLER_ACTOR_VERTEX, "interfaceCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffViaInterface()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testDefaultInterfaceCall() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "interfaceDefaultCall(" + PACKAGE_NAME + ".DoOtherStuff)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffDefault()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testThreadRunCall() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "threadRunReference()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testInterfaceImpl() {
        Link expectedLink1 = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffViaInterface()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffViaInterface()").build(), LinkType.INTERFACE_IMPL);

        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoStuffBase", "doStuffViaInterface()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "doStuffViaInterface()").build(),
                LinkType.INTERFACE_IMPL);
        testImplementingVisitor(expectedLink1, expectedLink2);
    }

    @Test
    public void testGenericReference() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "genericReference(java.util.List)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "instanceFieldReferenceGet()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "value").build(),
                LinkType.FIELD_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testFieldImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Callee", "value").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller", "instanceFieldReferencePut()").build(),
                LinkType.FIELD_PUT);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplGet() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "staticFieldReferenceGet()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "valueStatic").build(),
                LinkType.STATIC_GET);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testStaticImplPut() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Callee", "valueStatic").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller", "staticFieldReferencePut()").build(),
                LinkType.STATIC_PUT);
        testCallerVisitor(expectedLink);
    }


    @Test
    public void testImplicitExecutorServiceExecuteLink(){
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "testExecutorServiceExecute()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testImplicitExecutorServiceSubmitLink(){
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "testExecutorServiceSubmit()").build(),
                new Vertex.Builder("java.lang.Runnable", "run()").build(),
                LinkType.INTERFACE_CALL);
        testCallerVisitor(expectedLink);
    }

    @Test
    public void testOnlyMethodAnnotatedWithPath(){
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testJavaxOnlyMethodAnnotated()").build(),
                new Vertex.Builder("TRACE http://localhost/*", "/basic-quote-requests/annotation-only-method")
                        .markSpecial()
                        .build()
                ,LinkType.HTTP_REQUEST);
        testJavaxControllerVisitor(expectedLink);
    }

    @Test
    public void testTestMethodDetection() {
        TestVertex tv = new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()",null).withIgnore(true).build();
        Link expectedLink = newLink(VertexInitTypes.START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testIgnoredMethodDetection() {
        TestVertex tv = new TestVertex.Builder(PACKAGE_NAME + ".EmptyTest", "testIgnoredMethod()", null).withIgnore(true).build();
        Link expectedLink = newLink(VertexInitTypes.START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testNotIgnoredMethodDetection() {
        TestVertex tv = new TestVertex.Builder(PACKAGE_NAME + ".EmptyTest", "testNotIgnoredMethod()", null).build();
        Link expectedLink = newLink(VertexInitTypes.START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testTestIgnoredMethodAndClassDetection() {
        TestVertex tv = new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testIgnoredCall()", null).withIgnore(true).build();
        Link expectedLink = newLink(VertexInitTypes.START_VERTEX, tv, LinkType.ENTRY_POINT);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testConstructorCallDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()",null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", MethodUtils.INIT).build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink);
    }
    @Test
    public void testStaticConstructorCallDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()", null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", MethodUtils.CLINIT).build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testBeforeMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()", null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", "before()").build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testAfterMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()", null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", "after()").build(),
                LinkType.METHOD_CALL);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testBeforeClassMethodsDetection() {
        Link expectedLink = newLink(new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()", null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", "beforeClass()").build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testAfterClassMethodsDetection() {
        Link expectedLink = newLink(
                new TestVertex.Builder(PACKAGE_NAME + ".PlainTest", "testExplicitCall()", null).build(),
                new Vertex.Builder(PACKAGE_NAME + ".PlainTest", "afterClass()").build(),
                LinkType.STATIC_CALL);
        testTestClassVisitor(expectedLink);
    }
    @Test
    public void testOverriddenMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuff()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller", "doSuperStuff()").build()
                , LinkType.OVERRIDDEN);
        testInheritanceVisitor(expectedLink);
    }

    @Test
    public void testInheritedMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "doSuperStuffCaller()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuffCaller()").build(),
                LinkType.FORWARD_TO);
        testInheritanceVisitor(expectedLink);
    }

    @Test
    public void testInheritedInterfaceMethodDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".DoOtherStuff", "doStuffViaInterfaceSuper()").build(),
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
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testHttpVerbCall()").build(),
                getGetHttpVertex("GET", "/scenario/explicitCall"),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationMapDetection() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testDisconnectedSpringAnnotationMap()").build(),
                new Vertex.Builder("GET" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionGet() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("GET" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPost() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("POST" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPatch() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("PATCH" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionPut() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("PUT" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionDelete() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("DELETE" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method")
                        .markSpecial()
                        .build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testSpringAnnotationNoMethodDetectionTrace() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testAnnotationNoMethodMapCall()").build(),
                new Vertex.Builder("TRACE" + " " + HttpConsts.HTTP_LOCALHOST, "/scenario/annotation-no-method").markSpecial().build(),
                LinkType.HTTP_REQUEST);
        testTestClassVisitor(expectedLink);
    }

    @Test
    public void testJavaxAnnotationMapDetection() {
        Link expectedLink = newLink(
                new Vertex.Builder(PACKAGE_NAME + ".HttpTest", "testDisconnectedJavaxAnnotationMap()").build(),
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
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".CallerBase", "doSuperStuff()").build(), VertexInitTypes.END_VERTEX, LinkType.EXIT_POINT);
        testAffectedVisitor(expectedLink);
    }

    @Test
    public void testReferenceInLambdaDetection() {
        Link expectedLink1 = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "referenceInLambdaSimple()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller", "lambda$referenceInLambdaSimple$0(java.lang.String)").build(),
                LinkType.DYNAMIC_CALL);

        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "lambda$referenceInLambdaSimple$0(java.lang.String)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink1, expectedLink2);
    }

    @Test
    public void testReferenceInLambdaWithClass() {
        Link expectedLink1 = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller", "referenceInLambdaWithClass()").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Caller$1", "test(java.lang.String)").build(),
                LinkType.METHOD_CALL);

        Link expectedLink2 = newLink(new Vertex.Builder(PACKAGE_NAME + ".Caller$1", "test(java.lang.String)").build(),
                new Vertex.Builder(PACKAGE_NAME + ".Callee", "toString()").build(),
                LinkType.METHOD_CALL);
        testCallerVisitor(expectedLink1, expectedLink2);
    }

    @Ignore("This will fail because we do not have enough info for metadata (i.e. variable) changes")
    @Test
    public void testAffectedFieldDetection() {
        Link expectedLink = newLink(new Vertex.Builder(PACKAGE_NAME + ".Callee", "value").build(), VertexInitTypes.END_VERTEX, LinkType.EXIT_POINT);
        testAffectedVisitor(expectedLink);
    }

    @Test(expected = ParseRuntimeException.class)
    public void testInvalidContextLocation() {
        Context context = new Context(new String[]{"non-existing-location"}, new String[]{"non-existing-location"}, "com.sandboni", new ChangeScopeImpl());
        Finder f = new BcelFinder(new ClassVisitor[]{new CallerClassVisitor()});
        f.findSafe(context);
    }

    @Test
    public void testPoCDiffChangeDetector() {
        PoCDiffChangeDetector detector = new PoCDiffChangeDetector();
        ChangeScope<Change> changes = detector.getChanges("1", "2");
        Assert.assertTrue(!changes.getAllAffectedClasses().isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testNoConstructorCall() {
        Link expectedLink = newLink(new Vertex.Builder( PACKAGE_NAME + ".BasicScenario", MethodUtils.INIT).build(),
                new Vertex.Builder(  PACKAGE_NAME + ".Callee", MethodUtils.INIT).build(),
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
}