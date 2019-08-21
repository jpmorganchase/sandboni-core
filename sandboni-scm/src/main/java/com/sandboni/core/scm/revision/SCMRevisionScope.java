package com.sandboni.core.scm.revision;

import org.eclipse.jgit.lib.ObjectId;

public class SCMRevisionScope implements RevisionScope<ObjectId> {
    private final ObjectId from;
    private final ObjectId to;

    public SCMRevisionScope(ObjectId from, ObjectId to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public ObjectId getFrom() {
        return from;
    }

    @Override
    public ObjectId getTo() {
        return to;
    }
}
