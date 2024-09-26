# Contributing to FEEL-Scala

:tada: First off, thanks for taking the time to contribute! :+1:

## How can I contribute?

### Reporting bugs

If you found a bug or unexpected behavior, please create a [new issue](https://github.com/camunda/feel-scala/issues). Before you create an issue, please make sure that there is no issue yet. Any information you provide on the issue would be helpful to solve it.

### Suggesting enhancements

If you have an idea of how to improve the project, please create a [new issue](https://github.com/camunda/feel-scala/issues). Describe your idea and the motivation behind it. To speed up the process, think about providing a pull request.

### Providing pull requests

Do you want to provide a bug fix or an inprovement? Great! :tada:

Before opening a pull request, make sure that there is a related issue. The issue helps to confirm that the behavior is unexpected, or the idea of the improvement is valid. (Following the rule "Talk, then code")

In order to verify that you don't break anything, you should build the whole project and run all tests. This also apply the code formatting.

## Building the project from source

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

## Styleguides

### Source code

Scala code is formatted using [Scalafmt](https://scalameta.org/scalafmt/). The formatting is integrated in the build process.

### Git commit messages

Commit messages should follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/#summary) format.

For example:

```
feat: add random() function

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
* `ci` - changes to the CI (e.g. to GitHub-related configs)

## Public API and backward compatibility

The FEEL engine is integrated into Camunda 7 and Camunda 8. It's important to keep the public API stable and stay backward compatible to avoid breaking the integration in Camunda 7/8 or the userspace (i.e. the application that uses FEEL expressions). 

Concrete:
* The API of the following classes must remain binary backward compatible
  * Any class in a package namespace that does not contain `impl`, especially
    * `FeelEngine`
    * The custom function mechanism
    * Value mappers
* The following behavior must remain as is
  * Ability to compile expressions independently of evaluation
  * Ability to compile and evaluate expressions at once
  * Expression evaluation
    * Input and return type handling of expressions, e.g. the returned type of an expression should not change
    * The result of an expression unless it is a clear bug according to the DMN specification
* Supported environments
  * Minimal Java version: 11 (Camunda 7)
  * Java 21 (Camunda 8)

Technically:
* The binary backward compatibility is validated using the [clirr-maven-plugin](https://www.mojohaus.org/clirr-maven-plugin/)
* The engine behavior is verified by the unit tests  
* The supported environments are checked via GibHub actions 

Any change or violation of the above must be accepted by the maintainers of Camunda 7 and Camunda 8 to avoid that a team/product getting "locked out".

## Building the documentation

The documentation is located in the `/docs` folder. It is built with [Docusaurus](https://v2.docusaurus.io/)

For development, use the following command (build + serve + auto-reload):
```
npm run start
```

The documentation is published using GitHub actions.

## Building a new release

> [!NOTE]
> Only for Camunda developers. 

Open the Camunda Slack channel [#ask-dmn-feel](https://camunda.slack.com/archives/C01QYD808A3). Click on the bookmark "Release FEEL-Scala" and follow the instructions. 🏗️
