package com.sandboni.core.scm.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public final class PorcelainApi {
    private PorcelainApi() { }

    public static <R> R call(Repository repository, ThrowingFunction<Git, R> action) {
        try (Git git = new Git(repository)) {
            return action.apply(git);
        }
    }
}
