package com.sandboni.core.scm.scope;

import com.sandboni.core.scm.proxy.filter.FileExtensions;
import com.sandboni.core.scm.proxy.filter.FileNames;
import com.sandboni.core.scm.utils.FileUtil;
import com.sandboni.core.scm.utils.RawUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class SCMChangeBuilder{

    private static final Logger log = LoggerFactory.getLogger(SCMChangeBuilder.class);

    public String path;
    public Set<Integer> changedLines;
    public ChangeType changeType;
    public String fileContent;
    public String repository = "";

    private Optional<FileExtensions> getFileExt(String filePath){
        Optional tmp = FileUtil.getExtension(filePath);
        if (!tmp.isPresent())
            return Optional.empty();

        String ext = tmp.get().toString();
        FileExtensions fe = FileExtensions.fromText(ext);

        if (Objects.isNull(fe)) return Optional.empty();
        return Optional.of(fe);
    }

    private final Function<String, Boolean> isPropertyFile = filePath -> {
        Optional<FileExtensions> extensions = getFileExt(filePath);
        return extensions.map(fileExtensions ->
                fileExtensions.in(FileExtensions.PROPERTIES, FileExtensions.PROPS, FileExtensions.YML))
                .orElse(false);
    };

    private final Function<String, Boolean> isBuildFile = filePath ->
            filePath.toLowerCase().endsWith(FileNames.POM.fileName());

    public SCMChangeBuilder with(Consumer<SCMChangeBuilder> function){
        function.accept(this);
        return this;
    }

    public Change build() {
        final Change change;

        if (changeType == ChangeType.DELETE) {
            change = new SCMChange(path, changedLines, changeType);
        } else if (isPropertyFile.apply(path)){
            Set<String> keys = RawUtil.grepKeys(fileContent, changedLines);
            change = new SCMChangeInProperties(path, changedLines, keys, changeType);
        } else if (isBuildFile.apply(path)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = null;
            try {
                model = reader.read(new FileReader(repository.isEmpty() ? path : repository + File.separator + path));
            } catch (Exception e) {
                log.debug("Error when creating pom model for {}, exception: ", path, e);
            }

            change = new SCMChangeInBuildFile(path, changedLines, changeType, fileContent, model);
        } else{
            change = new SCMChange(path, changedLines, changeType);
        }
        return change;
    }
}
