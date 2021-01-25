package com.sandboni.core.engine.render.file;

import com.sandboni.core.engine.exception.RendererException;
import com.sandboni.core.engine.render.file.csv.CSVFileStrategy;
import com.sandboni.core.engine.render.file.csv.RelatedTestsFileRenderer;
import com.sandboni.core.engine.render.file.json.JSONFileRenderer;
import com.sandboni.core.engine.render.file.json.JSONFileStrategy;
import com.sandboni.core.engine.render.file.properties.PropertiesFileRenderer;
import com.sandboni.core.engine.render.file.properties.PropertiesFileStrategy;
import com.sandboni.core.engine.render.file.writer.DefaultFileWriter;
import com.sandboni.core.engine.render.file.writer.FileWriter;
import com.sandboni.core.engine.render.file.writer.PropertiesFileWriter;
import com.sandboni.core.engine.render.file.xml.XMLFileRenderer;
import com.sandboni.core.engine.render.file.xml.XMLFileStrategy;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FileWriterEngine {

    private FileWriterEngine() {
    }

    public static void write(Object result, FileOptions options) throws RendererException {
        Objects.requireNonNull(result);
        Object content = Objects.requireNonNull(getFileStrategy(options, result)).render();

        FileWriter fileWriter = getFileWriter(options.getType());
        fileWriter.write(new File(options.getName()).toPath(), content);
    }

    private static FileWriter getFileWriter(FileType type) {
        if (type == FileType.PROPERTIES) {
            return new PropertiesFileWriter();
        }
        return new DefaultFileWriter();
    }

    @SuppressWarnings("unchecked")
    private static FileStrategy getFileStrategy(FileOptions fileOptions, Object result) {
        FileType type = fileOptions.getType();
        if (type == FileType.CSV) {
            return new CSVFileStrategy<>((Set<TestVertex>) result, new RelatedTestsFileRenderer(), fileOptions);
        } else if (type == FileType.JSON) {
            return new JSONFileStrategy<>((Collection<Object>) result, new JSONFileRenderer<>(), fileOptions);
        } else if (type == FileType.PROPERTIES) {
            return new PropertiesFileStrategy<>((Map<String, Object>) result, new PropertiesFileRenderer<>(), fileOptions);
        } else { // type == FileType.XML
            return new XMLFileStrategy<>((Collection<Object>) result, new XMLFileRenderer(), fileOptions);
        }
    }
}
