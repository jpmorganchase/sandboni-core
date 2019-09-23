package com.sandboni.core.engine.sta.graph.vertex;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JiraVertex extends Vertex {

    private static final long serialVersionUID = -3983167557440987891L;

    private final Date date;
    private final String revisionId;

    @SuppressWarnings("squid:S00107") // private constructor used only in Builder
    private JiraVertex(String actor, String action, boolean isSpecial, String filePath, List<Integer> lineNumbers,
                       String filter, String location, Date date, String revisionId) {
        super(actor, action, isSpecial, filePath, lineNumbers, filter, location);
        this.date = date;
        this.revisionId = revisionId;
    }


    public static class Builder extends AbstractVertexBuilder<Builder>{
        private Date date;
        private String revisionId;

        public Builder(String actor, String action) {
            super(actor, action);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withDate(Date date){
            this.date = date;
            return getThis();
        }

        public Builder withRevisionId(String revisionId){
            this.revisionId = revisionId;
            return getThis();
        }

        @Override
        public JiraVertex build(){
            return new JiraVertex(this.actor, this.action, true, this.filePath, this.lineNumbers,
                    this.filter, null, this.date, this.revisionId);
        }
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public String getRevisionId() {
        return revisionId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JiraVertex vertex = (JiraVertex) o;
        return isSpecial() == vertex.isSpecial() &&
                Objects.equals(getActor(), vertex.getActor()) &&
                Objects.equals(getAction(), vertex.getAction()) &&
                Objects.equals(revisionId, vertex.revisionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getActor(), getAction(), isSpecial(), revisionId);
    }

}
