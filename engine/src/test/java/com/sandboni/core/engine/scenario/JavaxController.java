package com.sandboni.core.engine.scenario;

import javax.ws.rs.*;

//@Api(value = "/basic-quote-requests", description = "Requests for Quotes", tags = {"Custom Deals"})
@Path("/basic-quote-requests")
public class JavaxController {

    @POST
    @Path("/req")
    public String submitRequest(String test) {
        return "";
    }

    @DELETE
    @Path("/delete")
    public String deleteRequest(String test) {
        return "";
    }


    @GET
    @Path("/{test}")
    public String getOne(String test) {
        return "";
    }

    @GET
    @Path("/")
    public String getAll(String test) {
        return "";
    }

    @PUT
    @Path("/update")
    public String updateRequest(String test) {
        return "";
    }

    @PUT
    @Path("/annotation")
    public String annotationMap(String test) {
        return "";
    }

}
