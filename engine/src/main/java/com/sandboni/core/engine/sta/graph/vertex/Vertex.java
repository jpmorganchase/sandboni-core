package com.sandboni.core.engine.sta.graph.vertex;

import java.io.Serializable;
import java.util.*;

public class Vertex implements Serializable {

    private static final long serialVersionUID = 63271547337417074L;
    private final String actor;
    private final String action;
    private final boolean isSpecial;
    private String location;
    private String filePath;
    private transient List<Integer> lineNumbers;

    protected Vertex(String actor, String action, boolean isSpecial, String filePath,
                     List<Integer> lineNumbers, String location) {
        this.actor = actor;
        this.action = action;
        this.isSpecial = isSpecial;
        this.filePath = filePath;
        this.lineNumbers = lineNumbers;
        this.location = location;
    }

    protected abstract static class AbstractVertexBuilder<T extends AbstractVertexBuilder<T>> {
        protected final String actor;
        protected final String action;
        protected boolean isSpecial;
        String filePath;
        List<Integer> lineNumbers;
        protected String location;

        public AbstractVertexBuilder(String actor, String action, String location){
            this.actor = actor;
            this.action = action;
            this.location = location;
        }

        public AbstractVertexBuilder(String actor, String action){
            this.actor = actor;
            this.action = action;
        }

        public T markSpecial(){
            this.isSpecial = true;
            return getThis();
        }

        public T withFilePath(String filePath){
            this.filePath = filePath;
            return getThis();
        }

        public T withLineNumbers(List<Integer> lineNumbers){
            this.lineNumbers = Collections.unmodifiableList(lineNumbers);
            return getThis();
        }

        protected abstract T getThis();

        public Vertex build() {
            return new Vertex(this.actor, this.action, this.isSpecial, this.filePath, this.lineNumbers,
                    this.location);
        }
    }

    public static class Builder extends AbstractVertexBuilder<Builder> {

        public Builder(String actor, String action, String location) {
            super(actor, action, location);
        }

        public Builder(String actor, String action) {
            super(actor, action);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

    }

    public boolean isLineNumbersEmpty() {
        return lineNumbers == null ||lineNumbers.isEmpty();
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }


    public boolean isSpecial() {
        return isSpecial;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<Integer> getLineNumbers() {
        return new ArrayList<>(lineNumbers);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String shortenName(String name) {
        if (name == null) {
            return "null";
        }
        return name;
    }

    @Override
    public String toString() {
        return shortenName(actor) + "/" + shortenName(action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actor, action, isSpecial());
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Vertex vertex = (Vertex) o;
        return isSpecial() == vertex.isSpecial() &&
                Objects.equals(actor, vertex.actor) &&
                Objects.equals(action, vertex.action);
    }
}