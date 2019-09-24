package com.sandboni.core.scm.scope.analysis;

import com.sandboni.core.scm.scope.Change;
import com.sandboni.core.scm.scope.ChangeType;

import java.util.Objects;

public class VersionScanner implements ChangeScanner {

    @Override
    public boolean scan(Change change) {
        if (change.getType() != ChangeType.MODIFY ||
                !change.getModel().isPresent() ||
                !change.getFileContent().isPresent()) {
            return false;
        }

        String[] rows = change.getFileContent().orElse("").split(System.lineSeparator());

        return change.getLinesChanged().stream().allMatch(i -> {
            String row = rows[i - 1].trim();
            int from = row.indexOf('>') + 1;
            int to = row.lastIndexOf('<');
            if (from >= to)  return false;

            String value = row.substring(from, to);

            if (Objects.nonNull(change.getModel().get().getParent())) {
                return (change.getModel().get().getParent().getVersion().equals(value));
            } else if (Objects.nonNull(change.getModel().get().getVersion())) {
                return (change.getModel().get().getVersion().equals(value));
            }
            return false;
        });
    }
}
