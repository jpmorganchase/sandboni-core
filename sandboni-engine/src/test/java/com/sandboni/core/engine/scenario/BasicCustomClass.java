package com.sandboni.core.engine.scenario;


import javax.ws.rs.Path;


public class BasicCustomClass {

    @Path("/aaa,/bbb,/ccc")
    public void customMethodMultipleUrls(){}
}
