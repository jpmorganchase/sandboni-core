package com.sandboni.core.engine.render.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class FileOptions {

    private FileOptions() { }

    private FileType type;

    private String name;

    private Map<String, String> attributes;

    public FileType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public static class FileOptionsBuilder {

        private FileType type;

        private String name;

        private String attributes;

        public FileOptionsBuilder with(Consumer<FileOptionsBuilder> builderFunction) {
            builderFunction.accept(this);
            return this;
        }

        public FileOptions build() {
            FileOptions fileOptions = new FileOptions();
            fileOptions.type = Objects.requireNonNull(type);
            fileOptions.name = Objects.requireNonNull(name);
            fileOptions.attributes = new HashMap<>();
            if (Objects.nonNull(attributes)) {
                Type typeToken = new TypeToken<Map<String, String>>(){}.getType();
                fileOptions.attributes.putAll(new Gson().fromJson(attributes, typeToken));
            }
            return fileOptions;
        }

    }
}
