package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;

import java.util.Arrays;

public final class ChangeScanners implements ChangeScanner {

    private final ChangeScanner[] scanners;

    static final ChangeScanner ALL = new ChangeScanners(new VersionScanner());

    public ChangeScanners(ChangeScanner... scanners){
        this.scanners = scanners;
    }

    @Override
    public boolean scan(Change change) {
        return Arrays.stream(scanners).allMatch(s -> s.scan(change));
    }
}
