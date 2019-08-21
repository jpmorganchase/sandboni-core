package com.sandboni.core.engine.sta.graph.vertex;

import java.util.List;
import java.util.Objects;

public class TestVertex extends Vertex {

    private static final long serialVersionUID = 4024645517577346150L;

    private final boolean ignore;
    private boolean externalLocation;

    @SuppressWarnings("squid:S00107")
    protected TestVertex(String actor, String action, boolean isSpecial, String filePath,
                       List<Integer> lineNumbers, String filter,
                       String location, boolean ignore,
                       boolean externalLocation) {
        super(actor, action, isSpecial, filePath, lineNumbers, filter, location);
        this.ignore = ignore;
        this.externalLocation = externalLocation;
    }

    protected static abstract class AbstractTestVertexBuilder<T extends AbstractTestVertexBuilder<T>> extends AbstractVertexBuilder<T> {

        protected boolean ignore;
        boolean externalLocation;

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

        public T markAsExternalLocation() {
            this.externalLocation = true;
            return getThis();
        }

        @Override
        public TestVertex build() {
            return new TestVertex(this.actor, this.action, this.isSpecial, this.filePath, this.lineNumbers,
                    this.filter, this.location, this.ignore, this.externalLocation);
        }

        protected abstract T getThis();
    }

    public static class Builder extends AbstractTestVertexBuilder<Builder> {

        public Builder(String actor, String action, String location) {
            super(actor, action, location);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    public boolean isIgnore() {
        return ignore;
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

    public boolean isExternalLocation() {
        return externalLocation;
    }

    public void setExternalLocation(boolean externalLocation) {
        this.externalLocation = externalLocation;
    }

}