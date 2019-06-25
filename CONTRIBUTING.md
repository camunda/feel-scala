# Contributing to FEEL-Scala

:tada: First off, thanks for taking the time to contribute! :+1:

## How Can I Contribute?

### Reporting Bugs

If you found a bug or an unexpected behevior then please create a [new issue](https://github.com/camunda/feel-scala/issues). Before creating an issue, make sure that there is no issue yet. Any information you provide in the issue, helps to solve it.

### Suggesting Enhancements

If you have an idea how to improve the project then please create a [new issue](https://github.com/camunda/feel-scala/issues). Describe your idea and the motivation behind it. In order to speed up the process, think about providing a pull request.

### Improving Documentation

If you see a way to improve the documentation (e.g. provide additional or missing information) then please open a new pull request which contains your changes. The documentation is located in the repository at `/docs/*.md`. 

### Providing Pull Requests

You want to provide a bug fix or an inprovement? Great! :tada:

Before opening a pull request, make sure that there is a related issue. The issue helps to confirm that the behavior is unexpected, or the idea of the improvement is valid. (Following the rule "Talk, then code")

In order to verify that you don't break anything, you should build the whole project and run all tests. This also apply the code formatting.

## Building the Project from Source 

You can build the project with [SBT](http://www.scala-sbt.org) or [Maven](http://maven.apache.org). Both build files should be kept in sync.

### Using SBT

In the root directory:

Run all tests with
```
sbt test
```

Or only for one module (e.g. engine) with
```
sbt engine/test
```

Build the JAR files with
```
sbt assembly
```

### Using Maven

In the root directory:

Run the tests with
```
mvn test
```

Build the JAR files with
```
mvn install
```

## Styleguides

### Source Code

Scala code is formatted using [Scalafmt](https://scalameta.org/scalafmt/). The formatting is integrated in the build process.

### Git Commit Messages

Commit messages should include a short description of the changes and reference the issue.

