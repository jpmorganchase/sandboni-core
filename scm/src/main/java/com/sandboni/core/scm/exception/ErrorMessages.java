package com.sandboni.core.scm.exception;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String UNABLE_TO_FIND_REPOSITORY = "Unable to find repository. Make sure the folder is under Git ";
    public static final String UNABLE_TO_RESOLVE_REVISIONS = "Unable to resolve revision(s)";
    public static final String UNABLE_TO_FIND_REMOTE_BRANCH = "Unable to find remote branch";
    public static final String REVISIONS_CANNOT_BE_SAME = "Revisions FROM and TO cannot be the same";
    public static final String SCAN_REVISIONS_EXCEPTION = "Exception during scanning revision scope";
    public static final String BLAME_EXCEPTION = "Exception during blaming file";
}
