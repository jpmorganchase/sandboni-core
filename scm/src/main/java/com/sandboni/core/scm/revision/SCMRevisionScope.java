package com.sandboni.core.scm.revision;

import com.sandboni.core.scm.exception.ErrorMessages;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import org.eclipse.jgit.lib.ObjectId;

import java.util.function.Consumer;

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

    public static class RevisionScopeBuilder {
        public ObjectId from;
        public ObjectId to;

        public RevisionScopeBuilder with(Consumer<RevisionScopeBuilder> function){
            function.accept(this);
            return this;
        }

        public SCMRevisionScope build(){
            validate(from, to);
            return new SCMRevisionScope(from, to);
        }

        private void validate(ObjectId from, ObjectId to) {
            if (from == null || from.equals(ObjectId.zeroId())) {
                throw new SourceControlRuntimeException(ErrorMessages.FROM_CANNOT_BE_NULL);
            }
            if (to == null) {
                throw new SourceControlRuntimeException(ErrorMessages.TO_CANNOT_BE_NULL);
            }
        }
    }
}
