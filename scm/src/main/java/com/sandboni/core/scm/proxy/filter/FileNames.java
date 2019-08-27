package com.sandboni.core.scm.proxy.filter;

public enum FileNames {

    JENKINS("Jenkinsfile"), GITIGNORE(".gitignore"), POM("pom.xml");

    public String fileName() {
        return fileName;
    }

    private String fileName;

    FileNames(String fileName) {
        this.fileName = fileName;
    }
}
