# Contributing to FEEL-Scala

:tada: First off, thanks for taking the time to contribute! :+1:

## How Can I Contribute?

### Reporting Bugs

If you found a bug or an unexpected behevior then please create a [new issue](https://github.com/camunda/feel-scala/issues). Before creating an issue, make sure that there is no issue yet. Any information you provide in the issue, helps to solve it.

### Suggesting Enhancements

If you have an idea how to improve the project then please create a [new issue](https://github.com/camunda/feel-scala/issues). Describe your idea and the motivation behind it. In order to speed up the process, think about providing a pull request.

### Improving Documentation

If you see a way to improve the documentation (e.g. provide additional or missing information) then please open a new pull request which contains your changes. Use the link on the page to edit it.

### Providing Pull Requests

You want to provide a bug fix or an inprovement? Great! :tada:

Before opening a pull request, make sure that there is a related issue. The issue helps to confirm that the behavior is unexpected, or the idea of the improvement is valid. (Following the rule "Talk, then code")

In order to verify that you don't break anything, you should build the whole project and run all tests. This also apply the code formatting.

## Building the Project from Source

You can build the project with [Maven](http://maven.apache.org). 

In the root directory:

Run the tests with
```
mvn test
```

Build the JAR files with
```
mvn install
```

## Building the Documentation

The documentation is located in the `/docs` folder. It is built with [Docusaurus](https://v2.docusaurus.io/)

For development, use the following command (build + serve + auto-reload):
```
npm run start
```

The documentation is published using GitHub actions.

## Building a new Release

As a Camunda developer, create a [new issue](https://github.com/camunda/feel-scala/issues/new/choose) of the type `New Release`. Follow all steps in the issue to build and publish a new release 🏗️

## Styleguides

### Source Code

Scala code is formatted using [Scalafmt](https://scalameta.org/scalafmt/). The formatting is integrated in the build process.

### Git Commit Messages

Commit messages should follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/#summary) format.

For example:

```
feat(builtin-function): add random() function

* add new built-in function random()
* it returns a random number between 0.0 and 1.0
```

Available commit types:

* `feat` - enhancements, new features
* `fix` - bug fixes
* `refactor` - non-behavior changes
* `test` - only changes in tests
* `docs` - changes in the documentation, readme, etc.
* `style` - apply code styles
* `build` - changes to the build (e.g. to Maven's `pom.xml`)
* `ci` - changes to the CI (e.g. to GitHub related configs)

## Public API and Backwards Compatibility

Changes to the following code and concepts are considered breaking changes in the sense of semantic versioning. That means, if you want to make such a change, this must result in a new major version of this library. For any such change, both teams maintaining this codebase (Zeebe and Runtime) must be informed and accept the change. This allows us to make sure both teams will be able to work with a new major release and no team gets "locked out".

* The API of the following classes must remain binary backwards compatible
  * Any class in a package namespace that does not contain `impl`, especially
    * `FeelEngine`
    * The custom function mechanism
    * Value mappers
* The following behavior must remain as is
  * Ability to compile expressions independently of evaluation
  * Ability to compile and evaluate expressions at once
  * Expression evaluation
    * Input and return type handling of expressions, e.g. the returned type of an expression should not change
    * The result of an expression unless it is a clear bug with respect to the FEEL specification
* Supported environments
  * Minimal Java version: 8 (Runtime team)
  * Java 11 (Zeebe team)
