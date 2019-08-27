package com.sandboni.core.engine.render.file.csv;

import com.sandboni.core.engine.sta.graph.vertex.TestVertex;

import java.text.MessageFormat;
import java.util.Set;

public class RelatedTestsFileRenderer extends AbstractCSVFileRenderer<Set<TestVertex>>{

    private static final MessageFormat rowFormat = new MessageFormat("{0},{1},{2},{3},{4}\n");

    public static final String IS_DISCONNECTED_TESTS = "isDisconnectedTests";

    public enum TestTypes {UNIT, INTEGRATION}

    private static final String DOT = ".";
    private static final String EMPTY_STR = "";

    public RelatedTestsFileRenderer() {
        this.rowFormatter = (e, attr) -> {
            TestVertex t = (TestVertex) e;

            if (t.isIgnore()) return EMPTY_STR;

            final String actor = t.getActor();

           String isConnected = attr.containsKey(IS_DISCONNECTED_TESTS) && Boolean.valueOf(attr.get(IS_DISCONNECTED_TESTS))? "N" : "Y";

            /* plain UTs */
            if (actor.contains(DOT)) {
                int lastDot = actor.lastIndexOf(DOT);
                return rowFormat.format(new String[]{TestTypes.UNIT.name(), actor.substring(0, lastDot), actor.substring(lastDot + 1), t.getAction(), isConnected});
            } else {
                /* Cucumber tests in the form of "this is the scenario one" */
                return rowFormat.format(new String[]{TestTypes.INTEGRATION.name(), actor, EMPTY_STR, EMPTY_STR, isConnected});
            }
        };
    }

    @Override
    public String renderHeader() {
        return "type,package,class,method,connected?\n";
    }

}
