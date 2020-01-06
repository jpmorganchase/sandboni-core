package com.sandboni.core.engine.sta.graph.vertex;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TestVertex extends Vertex {

    private static final long serialVersionUID = 4024645517577346150L;

    private final boolean ignore;
    private boolean externalLocation;
    private boolean alwaysRun;
    private String runWithOptions;

    @SuppressWarnings("squid:S00107") // private constructor used only in Builder
    protected TestVertex(String actor, String action, boolean isSpecial, String filePath,
                       List<Integer> lineNumbers,
                       String location, boolean ignore,
                       boolean externalLocation, boolean alwaysRun, String runWithOptions) {
        super(actor, action, isSpecial, filePath, lineNumbers, location);
        this.ignore = ignore;
        this.externalLocation = externalLocation;
        this.alwaysRun = alwaysRun;
        this.runWithOptions = runWithOptions;
    }

    protected abstract static class AbstractTestVertexBuilder<T extends AbstractTestVertexBuilder<T>> extends AbstractVertexBuilder<T> {

        protected boolean ignore;
        boolean externalLocation;
        boolean alwaysRun;
        String runWithOptions;

        public AbstractTestVertexBuilder(String actor, String action, String location) {
            super(actor, action, location);
        }

        public AbstractTestVertexBuilder(String actor, String action) {
            super(actor, action);
        }

        public T withIgnore(boolean ignore) {
            this.ignore = ignore;
            return getThis();
        }

        public T withAlwaysRun(boolean alwaysRun) {
            this.alwaysRun = alwaysRun;
            return getThis();
        }

        public T withRunWithOptions(String runWithOptions) {
            this.runWithOptions = runWithOptions;
            return getThis();
        }

        public T markAsExternalLocation() {
            this.externalLocation = true;
            return getThis();
        }

        @Override
        public TestVertex build() {
            return new TestVertex(this.actor, this.action, this.isSpecial, this.filePath, this.lineNumbers,
                    this.location, this.ignore, this.externalLocation, this.alwaysRun, this.runWithOptions);
        }

        protected abstract T getThis();
    }

    public static class Builder extends AbstractTestVertexBuilder<Builder> {

        public Builder(String actor, String action) {
            this(actor, action, null);
        }

        public Builder(String actor, String action, String location) {
            super(actor, action, location);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestVertex vertex = (TestVertex) o;
        return isSpecial() == vertex.isSpecial() &&
                Objects.equals(getActor(), vertex.getActor()) &&
                Objects.equals(getAction(), vertex.getAction()) &&
                ignore == vertex.ignore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActor(), getAction(), isSpecial(), ignore);
    }

}