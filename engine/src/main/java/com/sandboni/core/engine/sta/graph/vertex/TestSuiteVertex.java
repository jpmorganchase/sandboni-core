package com.sandboni.core.engine.sta.graph.vertex;

import java.util.List;

public class TestSuiteVertex extends TestVertex {

    protected TestSuiteVertex(String actor, String action, boolean isSpecial, String filePath, List<Integer> lineNumbers, String filter, String location, boolean ignore, boolean externalLocation, boolean included) {
        super(actor, action, isSpecial, filePath, lineNumbers, filter, location, ignore, externalLocation, included);
    }

    public static class Builder extends AbstractTestVertexBuilder<Builder> {

        public Builder(String actor, String action, String location) {
            super(actor, action, location);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public TestSuiteVertex build(){
            return new TestSuiteVertex(this.actor, this.action, true, this.filePath, this.lineNumbers,
                    this.filter, this.location, this.ignore, this.externalLocation, this.included);
        }
    }


}
