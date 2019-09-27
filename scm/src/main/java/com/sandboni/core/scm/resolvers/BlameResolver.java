package com.sandboni.core.scm.resolvers;

import com.sandboni.core.scm.exception.ErrorMessages;
import com.sandboni.core.scm.exception.SourceControlException;
import com.sandboni.core.scm.revision.RevInfo;
import com.sandboni.core.scm.utils.PorcelainApi;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlameResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlameResolver.class);
    private static final String JIRA_ID_REGEX = "((?<!([A-Z]{1,10})-?)[A-Z]+-\\d+)";

    private final Repository repository;
    private final TreeWalkResolver treeWalkResolver;
    private final String revStr;

    public BlameResolver(Repository repository, String revStr) {
        this.repository = repository;
        this.revStr = revStr;
        this.treeWalkResolver = new TreeWalkResolver(repository, revStr);
    }

    public Set<RevInfo> blame(String packagePath, List<Integer> lineNumbers) throws SourceControlException {
        try {
            String filepath = treeWalkResolver.getFullPath(packagePath);
            if (filepath == null) {
                LOGGER.warn("Unable to find package: {}", packagePath);
                return Collections.emptySet();
            }
            Set<RevInfo> revInfoSet = new HashSet<>();
            BlameResult blameResult = PorcelainApi.call(repository, git -> git.blame().setFilePath(filepath).setStartCommit(repository.resolve(revStr)).call());
            if (blameResult == null) {
                LOGGER.warn("Blame result is empty for filepath: {}", filepath);
                return Collections.emptySet();
            }
            int size = blameResult.getResultContents().size();
            for (Integer i : lineNumbers) {
                if (i <= size) {
                    RevCommit revision = blameResult.getSourceCommit(i - 1);
                    RevInfo revInfo = getRevInfo(revision);
                    revInfoSet.add(revInfo);
                }
            }
            LOGGER.debug("Blame result for package {} with lines {} is\n {}", packagePath, lineNumbers, revInfoSet);
            return revInfoSet;
        } catch (IOException e) {
            throw new SourceControlException(ErrorMessages.BLAME_EXCEPTION, e);
        }
    }

    private RevInfo getRevInfo(final RevCommit commit) {
        return new RevInfo(commit.getAuthorIdent().getWhen(), cutJiraIdFromMessage(commit.getShortMessage()), commit.getName());
    }

    private String cutJiraIdFromMessage(String message) {
        Matcher matcher = Pattern.compile(JIRA_ID_REGEX).matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return message;
    }
}
