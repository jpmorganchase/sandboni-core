package com.sandboni.core.scm;

import com.sandboni.core.scm.exception.ErrorMessages;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.exception.SourceControlRuntimeException;
import com.sandboni.core.scm.proxy.SourceControlFilter;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeScope;
import com.sandboni.core.scm.resolvers.BlameResolver;
import com.sandboni.core.scm.resolvers.ChangeScopeResolver;
import com.sandboni.core.scm.resolvers.RevisionResolver;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class GitRepository implements GitInterface {
    private final RevisionResolver revisionResolver;
    private final ChangeScopeResolver changeScopeResolver;
    private final BlameResolver blameResolver;

    public GitRepository(String repositoryPath, SourceControlFilter... filters) {
        Repository repository = buildRepository(repositoryPath);
        this.revisionResolver = new RevisionResolver(repository);
        this.changeScopeResolver = new ChangeScopeResolver(repository, filters);
        this.blameResolver = new BlameResolver(repository, Constants.HEAD);
    }

    @Override
    public ChangeScope<Change> getChanges(String fromRev, String toRev) throws SourceControlException {
        RevisionScope<ObjectId> revisionScope = revisionResolver.resolve(fromRev, toRev);
        return changeScopeResolver.getChangeScope(revisionScope);
    }

    @Override
    public Set<RevInfo> getJiraSet(String packagePath, List<Integer> lineNumbers) throws SourceControlException {
        return blameResolver.blame(packagePath, lineNumbers);
    }

    public static Repository buildRepository(String repositoryPath) {
        FileRepositoryBuilder fileRepositoryBuilder = new FolderRepositoryBuilder(repositoryPath);
        try {
            return new FileRepository(fileRepositoryBuilder);
        } catch (IOException e) {
            throw new SourceControlRuntimeException(ErrorMessages.UNABLE_TO_FIND_REPOSITORY + repositoryPath, e);
        }
    }

    public static class FolderRepositoryBuilder extends FileRepositoryBuilder {
        FolderRepositoryBuilder(String path) {
            try {
                File dir = new File(path);
                this.addCeilingDirectory(dir)
                        .findGitDir(dir)
                        .build();
            } catch (Exception e) {
                throw new SourceControlRuntimeException(ErrorMessages.UNABLE_TO_FIND_REPOSITORY + path, e);
            }
        }
    }
}
