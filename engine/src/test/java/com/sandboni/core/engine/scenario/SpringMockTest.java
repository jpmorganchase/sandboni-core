package com.sandboni.core.engine.scenario;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Ignore("This is a test for actual tests to discover")
public class SpringMockTest {


    @Autowired
    private WebApplicationContext webApplicationContext;


    private static MockMvc mockMvc;

    @Before
    public void Before(){
        if (mockMvc == null)
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testMockGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/scenario/mockGet", ""));
        assertTrue(true);
    }

    @Test
    public void testMockPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/scenario/mockPost", ""));
        assertTrue(true);
    }

    @Test
    public void testMockPatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/scenario/mockPatch", ""));
        assertTrue(true);

    }

    @Test
    public void testMockDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/scenario/mockDelete", ""));
        assertTrue(true);
    }

    @Test
    public void testMockPut() throws Exception {
        String v = "/scenario/mockPutWWW";
        mockMvc.perform(MockMvcRequestBuilders.put("/scenario/mockPut", ""));
        assertTrue(true);
    }
}
