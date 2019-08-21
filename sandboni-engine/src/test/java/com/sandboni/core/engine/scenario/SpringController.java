package com.sandboni.core.engine.scenario;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SpringController {

    @RequestMapping(path = "/scenario/explicitCall", method = RequestMethod.GET)
    public String explicitCall() {
        Caller c = new Caller();
        c.explicitCall();
        return "OK";
    }

    @RequestMapping(path = "/scenario/annotation", method = RequestMethod.GET)
    public String annotationMapCall() {
        return "OK";
    }


    @RequestMapping(path = "/scenario/annotation-no-method")
    public String annotationNoMethodMapCall() {
        return "OK";
    }

    @GetMapping(path = "/scenario/annotation-get-method")
    public String annotationGetMethodMapCall() {
        return "OK";
    }

    @PostMapping(path = "/scenario/annotation-post-method")
    public String annotationPostMethodMapCall() {
        return "OK";
    }

    @DeleteMapping(path = "/scenario/annotation-delete-method")
    public String annotationDeleteMethodMapCall() {
        return "OK";
    }

    @PutMapping(path = "/scenario/annotation-put-method")
    public String annotationPutMethodMapCall() {
        return "OK";
    }

    @PatchMapping(path = "/scenario/annotation-patch-method")
    public String annotationPatchMethodMapCall() {
        return "OK";
    }


    //--MockMvc

    @GetMapping("/scenario/mockGet")
    public String receiveGetFromMock(){
        return "OK";
    }

    @PostMapping("/scenario/mockPost")
    public String receivePostFromMock(){
        return "OK";
    }

    @PutMapping("/scenario/mockPut")
    public String receivePutFromMock(){
        return "OK";
    }

    @PatchMapping("/scenario/mockPatch")
    public String receivePatchFromMock(){
        return "OK";
    }

    @DeleteMapping("/scenario/mockDelete")
    public String receiveDeleteFromMock(){
        return "OK";
    }
}
