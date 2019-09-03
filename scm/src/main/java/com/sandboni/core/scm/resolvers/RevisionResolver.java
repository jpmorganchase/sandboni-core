package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.exception.ErrorMessages;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.DiffConstants;
import com.sandboni.core.scm.revision.RevisionScope;
import com.sandboni.core.scm.revision.SCMRevisionScope;
import org.eclipse.jgit.lib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class RevisionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RevisionResolver.class);
    private Repository repository;

    public RevisionResolver(Repository repository) {
        this.repository = repository;
    }

    public RevisionScope<ObjectId> resolve(String fromRev, String toRev) throws SourceControlException {
        ObjectId from = resolve(fromRev);
        ObjectId to = resolve(toRev);
        validate(from, to);
        return new SCMRevisionScope(from, to);
    }

    private ObjectId resolve(String key) throws SourceControlException {
        if (key == null) {
            LOGGER.warn("Key cannot be null");
            throw new SourceControlException(ErrorMessages.UNABLE_TO_RESOLVE_REVISIONS);
        }
        try {
            switch (key) {
                case DiffConstants.LOCAL_CHANGES_NOT_COMMITTED:
                    return getToBeCommitted();
                case DiffConstants.LATEST_PUSH:
                    return getLatestPush();
                case DiffConstants.LATEST_COMMIT:
                    return getLatestCommit();
                default:
                    return getCommitOrTag(key);
            }
        } catch (Exception e) {
            throw new SourceControlException(ErrorMessages.UNABLE_TO_RESOLVE_REVISIONS, e);
        }
    }

    private ObjectId getCommitOrTag(String key) throws IOException {
        return key.startsWith(Constants.R_REFS)
                ? getObjectIdFromTag(key)
                : repository.resolve(key);
    }

    private ObjectId getToBeCommitted() {
        return ObjectId.zeroId();
    }

    private ObjectId getLatestCommit() throws IOException {
        return repository.resolve(Constants.HEAD);
    }

    private ObjectId getObjectIdFromTag(String referenceName) throws IOException {
        Ref reference = repository.exactRef(referenceName);
        return reference.getObjectId();
    }

    private ObjectId getLatestPush() throws IOException {
        String remoteBranch = getRemoteBranch();
        return repository.resolve(remoteBranch);
    }

    private String getRemoteBranch() throws IOException {
        String branch = repository.getBranch();
        BranchConfig branchConfig = new BranchConfig(repository.getConfig(), branch);
        String trackedBranch = branchConfig.getRemoteTrackingBranch();
        if (trackedBranch == null) {
            if (tryResolveBranch(branch)) {
                LOGGER.debug("...branch {} can be resolved", branch);
                return branch;
            }
            LOGGER.debug("...Couldn't find trackedBranch - looking for alternative approach");
            String fetch = getRemoteOriginFetchFromConfig();
            trackedBranch = tryGetRemoteOriginBranch(branch, fetch);
        }
        LOGGER.info("Tracked branch: {}:{}", trackedBranch, branch);
        return trackedBranch;
    }

    private boolean tryResolveBranch(String branch) throws IOException {
        return repository.resolve(branch) != null;
    }

    private String getRemoteOriginFetchFromConfig() {
        return repository.getConfig().getString("remote", Constants.DEFAULT_REMOTE_NAME, "fetch");
    }

    private String tryGetRemoteOriginBranch(String branch, String fetch) throws IOException {
        if (Objects.isNull(fetch) || fetch.isEmpty()) {
            LOGGER.debug("...Unable to locate section [remote|origin|fetch]");
            throw new IOException(ErrorMessages.UNABLE_TO_FIND_REMOTE_BRANCH);
        }

        LOGGER.debug("...[remote|origin|fetch] the value is : {}", fetch);
        final String REFS_REMOTE_ORIGIN = "refs/remotes/origin/";
        Optional<String> res = Arrays.stream(fetch.split(":")).filter(f -> f.contains(REFS_REMOTE_ORIGIN)).findFirst();
        if (!res.isPresent()) {
            LOGGER.debug("... none of [{}] elements doesn't contain '{}'", fetch, REFS_REMOTE_ORIGIN);
            throw new IOException(ErrorMessages.UNABLE_TO_FIND_REMOTE_BRANCH);
        }

        /*
          Options for fetch values:
          before splitting:
          (1) +refs/heads/*:refs/remotes/origin/*
          (2) +refs/heads/feature/MACSMARKETS-9223:refs/remotes/origin/feature/MACSMARKETS-9223

          after splitting:
          (1) refs/remotes/origin/*  -> in that case substring (start, '*') + add branch name
          (2) refs/remotes/origin/feature/MACSMARKETS-9223 ->no '*', take as is
         */

        String trackedBranch = res.get();
        int starIndex = trackedBranch.indexOf('*');
        if (starIndex > 0) {
            trackedBranch = trackedBranch.substring(0, starIndex) + branch;
        }
        return trackedBranch;
    }

    private void validate(ObjectId from, ObjectId to) throws SourceControlException {
        LOGGER.info("Resolved revision scope: {} : {}", from, to);
        if (from == null || to == null || from.equals(ObjectId.zeroId())) {
            throw new SourceControlException(ErrorMessages.UNABLE_TO_RESOLVE_REVISIONS);
        }
        if (from.equals(to)) {
            throw new SourceControlException(ErrorMessages.REVISIONS_CANNOT_BE_SAME);
        }
    }
}
