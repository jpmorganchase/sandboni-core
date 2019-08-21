package com.sandboni.core.engine.scenario;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import static org.junit.Assert.assertTrue;

@Ignore("This is a test for actual tests to discover")
public class HttpTest {
    private static final String url = "/basic-quote-requests/";

    @Test
    public void testHttpVerbCall() {
        String ss = "/some/pre/noise";
        String s = new HttpClient().get("/scenario/explicitCall");
        String s1 = "/some/post/noise";
        assertTrue(true);
    }

    @Test
    public void testPostToUnknownRootPrefix() {
        new HttpClient().post("/some-prefix-from-configuration/basic-quote-requests/req");
        assertTrue(true);
    }

    @Test
    public void testJavaXParam() {
        String id = "1";
        new HttpClient().get("/some-prefix-from-configuration/basic-quote-requests/" + id);
        assertTrue(true);
    }

    @Test
    public void testJavaXLevel() {
        String s = new HttpClient().get("/some-prefix-from-configuration/basic-quote-requests/");
        assertTrue(true);
    }

    @Test
    public void testDisconnectedManualMap() {
        String url = String.format("%s/%s/%s", "prefix", "basic-quote-requests", "update");
        new HttpClient().get(url);
        assertTrue(true);
    }

    @Test
    @PUT
    @Path("/basic-quote-requests/annotation")
    public void testDisconnectedJavaxAnnotationMap() {
        assertTrue(true);
    }

    @Test
    @RequestMapping(path = "/scenario/annotation", method = RequestMethod.GET)
    public void testDisconnectedSpringAnnotationMap() {
        assertTrue(true);
    }


    @Test
    @RequestMapping(path = "/scenario/annotation-no-method")
    public void testAnnotationNoMethodMapCall() {
        assertTrue(true);
    }

    @Test
    public void testJavaXReference() {
        String s = new HttpClient().get(url);
        assertTrue(true);
    }

    class HttpClient {
        String get(String url) {
            return "OK";
        }

        String post(String url) {
            return "OK";
        }
    }

    @Path("/basic-quote-requests/annotation-only-method")
    public void testJavaxOnlyMethodAnnotated(){
        assertTrue(true);
    }
}
