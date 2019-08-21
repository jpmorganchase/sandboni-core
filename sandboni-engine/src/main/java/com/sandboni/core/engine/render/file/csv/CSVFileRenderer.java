package com.sandboni.core.engine.render.file.csv;

import com.sandboni.core.engine.render.file.FileRenderer;

import java.util.Collection;

public interface CSVFileRenderer<R extends Collection> extends FileRenderer<R, String> {

    String renderHeader();

}
