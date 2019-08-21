package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.utils.FileUtil;
import com.sandboni.core.scm.utils.RawUtil;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class SCMChangeBuilder{

    public String path;
    public Set<Integer> changedLines;
    public ChangeType changeType;
    public String fileContent;

    public final Function<String, Boolean> isPropertyFile = filePath -> {
        Optional tmp = FileUtil.getExtension(filePath);
        if (!tmp.isPresent()) return false;

        String ext = tmp.get().toString();

        FileExtensions extension = FileExtensions.fromText(ext);
        if (extension == null) return false;

        return extension.in(FileExtensions.PROPERTIES, FileExtensions.PROPS, FileExtensions.YML);
    };

    public SCMChangeBuilder with(Consumer<SCMChangeBuilder> function){
        function.accept(this);
        return this;
    }

    public Change build(){
        final Change change;

        if (isPropertyFile.apply(path)){
            Set<String> keys = RawUtil.grepKeys(fileContent, changedLines);
            change = new SCMChangeInProperties(path, changedLines, keys, changeType);
        } else {
            change = new SCMChange(path, changedLines, changeType);
        }
        return change;
    }
}
