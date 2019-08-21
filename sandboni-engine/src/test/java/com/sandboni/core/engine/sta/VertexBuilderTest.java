package com.sandboni.core.engine.sta;

import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.JiraVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class VertexBuilderTest {

    private static final String ACTOR = "actor";
    private static final String ACTION = "action";
    private static final String LOCATION = "location";


    @Test
    public void testVertexBuilder() {
        Vertex v = new Vertex.Builder(ACTOR, ACTION, LOCATION)
                .withFilePath("filePath")
                .withLineNumbers(new ArrayList<>())
                .withFilter("filter")
                .build();


        Assert.assertEquals(ACTOR, v.getActor());
        Assert.assertEquals(ACTION, v.getAction());
        Assert.assertEquals(LOCATION, v.getLocation());
        Assert.assertEquals("filePath", v.getFilePath());
        Assert.assertEquals("filter", v.getFilter());
        Assert.assertTrue(v.isLineNumbersEmpty());
        Assert.assertFalse(v.isSpecial());
    }

    @Test
    public void testMarkSpecial() {
        Vertex v = new Vertex.Builder(ACTOR, ACTION, LOCATION)
                .markSpecial()
                .build();
        Assert.assertTrue(v.isSpecial());
    }

    @Test
    public void testTestVertexBuilder(){
        TestVertex v = new TestVertex.Builder(ACTOR, ACTION, LOCATION)
                .withIgnore(true)
                .markAsExternalLocation()
                .build();
        Assert.assertTrue(v.isExternalLocation());
        Assert.assertTrue(v.isIgnore());
    }

    @Test
    public void testCucumberVertexBuilder(){
        CucumberVertex v = new CucumberVertex.Builder(ACTOR, ACTION)
                .withScenarioLine(1)
                .withFeaturePath("featurePath")
                .markAffected(true)
                .build();
        Assert.assertTrue(v.isAffected());
        Assert.assertEquals("featurePath", v.getFeaturePath());
        Assert.assertEquals(1, v.getScenarioLine());
    }

    @Test
    public void testJiraVertexBuilder(){
        Date d = new Date();
        JiraVertex v = new JiraVertex.Builder(ACTOR, ACTION)
                .withRevisionId("revisionId")
                .withDate(d)
                .build();

        Assert.assertEquals("revisionId", v.getRevisionId());
        Assert.assertEquals(d, v.getDate());

    }


}
