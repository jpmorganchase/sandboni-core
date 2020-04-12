package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import org.eclipse.jgit.lib.ObjectId;

public interface ChangeScopeResolver {

    ChangeScope<Change> getChangeScope(RevisionScope<ObjectId> revisionScope) throws SourceControlException;
}
