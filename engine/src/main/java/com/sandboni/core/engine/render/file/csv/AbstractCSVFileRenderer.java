package com.sandboni.core.engine.render.file.csv;

import com.sandboni.core.engine.render.file.FileOptions;
import com.sandboni.core.engine.render.file.RowFormatter;

import java.util.Collection;

public abstract class AbstractCSVFileRenderer<R extends Collection> implements CSVFileRenderer<R>{

    protected RowFormatter<String> rowFormatter;

    AbstractCSVFileRenderer(){ }

    public String renderBody(R resultObj, FileOptions fileOptions){
        StringBuilder builder = new StringBuilder(renderHeader());
        resultObj.forEach(o-> builder.append(rowFormatter.format(o, fileOptions.getAttributes())));

        return builder.toString();
    }
}
