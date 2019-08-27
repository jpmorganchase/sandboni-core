package com.sandboni.core.scm.revision;

import java.util.Date;
import java.util.Objects;

public class RevInfo {
    private Date date;
    private String jiraId;
    private String revisionId;

    public RevInfo(Date date, String jiraId, String revisionId) {
        this.date = (Date) date.clone();
        this.jiraId = jiraId;
        this.revisionId = revisionId;
    }

    public Date getDate() {
        return (Date) date.clone();
    }

    public String getRevisionId() {
        return revisionId;
    }

    public String getJiraId() {
        return jiraId;
    }

    @Override
    public String toString() {
        return "[" + date + " | " + revisionId + " | " + jiraId + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, jiraId, revisionId);
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RevInfo vertex = (RevInfo) o;
        return getDate() == vertex.getDate() &&
                Objects.equals(revisionId, vertex.getRevisionId()) &&
                Objects.equals(jiraId, vertex.getJiraId());
    }
}
