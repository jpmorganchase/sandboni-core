package com.sandboni.core.engine.sta.graph.vertex;

import java.util.List;
import java.util.Objects;

public class CucumberVertex extends TestVertex {

    private static final long serialVersionUID = -8755419134897997444L;

    private final String featurePath;
    private final int scenarioLine;
    private boolean affected;

    @SuppressWarnings("squid:S00107") // private constructor used only in Builder
    private CucumberVertex(String actor, String action, boolean isSpecial, String filePath, List<Integer> lineNumbers,
                          String location, boolean ignore, boolean externalLocation, String featurePath, int scenarioLine, boolean affected) {
        super(actor, action, isSpecial, filePath, lineNumbers, location, ignore, externalLocation, false, null);
        this.featurePath = featurePath;
        this.scenarioLine = scenarioLine;
        this.affected = affected;
    }

    public static class Builder extends AbstractTestVertexBuilder<Builder> {

        private String featurePath;
        private int scenarioLine;
        private boolean affected;

        public Builder(String actor, String action) {
            super(actor, action);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withFeaturePath(String featurePath){
            this.featurePath = featurePath;
            return getThis();
        }

        public Builder withScenarioLine(int scenarioLine){
            this.scenarioLine = scenarioLine;
            return getThis();
        }

        public Builder markAffected(boolean affected){
            this.affected = affected;
            return this;
        }

        @Override
        public CucumberVertex build(){
            return new CucumberVertex(this.actor, this.action, true, this.filePath, this.lineNumbers,
                    this.location, this.ignore, this.externalLocation, this.featurePath, this.scenarioLine, this.affected);
        }
    }

    public String getFeaturePath() {
        return featurePath;
    }

    public int getScenarioLine() {
        return scenarioLine;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CucumberVertex vertex = (CucumberVertex) o;
        return isSpecial() == vertex.isSpecial() &&
                Objects.equals(getActor(), vertex.getActor()) &&
                Objects.equals(getAction(), vertex.getAction()) &&
                Objects.equals(featurePath, vertex.featurePath) &&
                scenarioLine == vertex.scenarioLine;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActor(), getAction(), isSpecial(), featurePath, scenarioLine);
    }

    public boolean isAffected() {
        return affected;
    }
}