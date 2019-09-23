# <img src="doc/logo.png" width="180" height="180"/>

<h1 align="center">Sandboni Core</h1>

[![Maven Central](https://img.shields.io/maven-central/v/com.github.jpmorganchase.sandboni/sandboni-engine.svg)](https://mvnrepository.com/artifact/com.github.jpmorganchase.sandboni/sandboni-engine)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://choosealicense.com/licenses/apache-2.0)
[![Build Status](https://travis-ci.com/jpmorganchase/sandboni-core.svg?branch=master)](https://travis-ci.com/jpmorganchase/sandboni-core)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jpmorganchase.sandboni%3Asandboni-core&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.jpmorganchase.sandboni%3Asandboni-core)

Sandboni is a `Java test optimization library` which reduces test execution time without 
compromising quality, predicts defect probability and presents actionable test results 
which allows developers to take preemptive measures.

Instead of applying a brute force approach to testing (i.e. executing all tests each time 
there is a code change), Sandboni determines the code slice affected by the change and 
executes only those tests related to the change. This test filtering is based on the static 
code analysis that lets us determine required tests without paying the price for executing them

## Features
* Compute a change scope (CS) between two revisions
* Build a change dependency graph (CDG) for a specific change scope
* Find and execute related/disconnected tests based on the CDG
* Support JVM and Cucumber tests

## Structure of code

The code is in a [multi-module](https://maven.apache.org/guides/mini/guide-multiple-modules.html) project structure

* scm - builds a change scope between two revisions 
    * [jGit](https://github.com/eclipse/jgit)
* engine - analyzes bytecode by locations, builds CDG and it contains the main algorithms for the graph traversing
    * [Bcel](https://github.com/apache/commons-bcel)
    * [JGraphT](https://github.com/jgrapht/jgrapht)

## Documentation

* [Quickstart](https://github.com/jpmorganchase/sandboni-core/wiki/Quickstart)
* [Design](https://github.com/jpmorganchase/sandboni-core/wiki/Sandboni-Design)
* [Contributing](https://github.com/jpmorganchase/sandboni-core/wiki/Contributing)

## Contributing
Sandboni is built on open source and we invite you to contribute enhancements. There are many ways in which you can participate in the project, for example:
                                                                               
- [Submit bugs and feature requests](https://github.com/jpmorganchase/sandboni-core/issues), and help us verify as they are checked in
- [Review source code changes](https://github.com/jpmorganchase/sandboni-core/pulls)
- Review the [documentation](https://github.com/jpmorganchase/sandboni-core/wiki) and make pull requests for new content

## License
The Apache License, Version 2.0

Please see [License](https://choosealicense.com/licenses/apache-2.0/) for more information