package com.sandboni.core.engine.sta.graph.vertex;

import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class TestSuiteVertex extends TestVertex {
    private Set<String> relatedTestClasses;

    @SuppressWarnings("squid:S00107") // private constructor used only in Builder
    private TestSuiteVertex(String actor, String action, boolean isSpecial, String filePath, List<Integer> lineNumbers, String filter, String location, boolean ignore, boolean externalLocation, boolean included, String runWithOptions,  Set<String> relatedTestClasses) {
        super(actor, action, isSpecial, filePath, lineNumbers, filter, location, ignore, externalLocation, included, runWithOptions);
        this.relatedTestClasses = relatedTestClasses;
    }

    public static class Builder extends AbstractTestVertexBuilder<Builder> {
        private Set<String> relatedTestClasses;

        public Builder(String actor, Set<String> relatedTestClasses, String location) {
            super(actor, "", location);
            this.relatedTestClasses = relatedTestClasses;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public TestSuiteVertex build(){
            return new TestSuiteVertex(this.actor, this.action, true, this.filePath, this.lineNumbers,
                    this.filter, this.location, this.ignore, this.externalLocation, this.included, this.runWithOptions, this.relatedTestClasses);
        }
    }


}
