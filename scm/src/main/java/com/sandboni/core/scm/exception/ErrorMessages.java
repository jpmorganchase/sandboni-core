package com.sandboni.core.scm.exception;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String UNABLE_TO_FIND_REPOSITORY = "Unable to find repository. Make sure the folder is under Git ";
    public static final String UNABLE_TO_RESOLVE_REVISIONS = "Unable to resolve revision(s)";
    public static final String UNABLE_TO_FIND_REMOTE_BRANCH = "Unable to find remote branch";
    public static final String FROM_CANNOT_BE_NULL = "Revision FROM cannot be null or LOCAL_CHANGES_NOT_COMMITTED";
    public static final String TO_CANNOT_BE_NULL = "Revision TO cannot be null";
    public static final String SCAN_REVISIONS_EXCEPTION = "Exception during scanning revision scope";
    public static final String BLAME_EXCEPTION = "Exception during blaming file";
}
