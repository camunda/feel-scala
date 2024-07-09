# FEEL Scala

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.camunda.feel/feel-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.camunda.feel/feel-engine)

A parser and interpreter for FEEL that is written in Scala (see [What is FEEL?](https://docs.camunda.io/docs/next/components/modeler/feel/what-is-feel/)).

The FEEL engine started as a slack time project, grown into a community-driven project, and is now officially maintained by [Camunda](https://camunda.org/). :rocket: 

It is integrated in the following projects:
* [Camunda 7](https://docs.camunda.org/manual/user-guide/dmn-engine/feel/) as part of the DMN engine
* [Camunda 8 (Zeebe)](https://docs.camunda.io/docs/product-manuals/concepts/expressions#the-expression-language) as the expression language
* [DMN-Scala](https://github.com/camunda/dmn-scala/) as part of the DMN engine

**Features:** :sparkles:

* Full support for unary-tests and expressions 
* Including built-in functions
* Extensible by custom functions and object mappers

## Usage 

Please have a look at the [documentation](https://docs.camunda.io/docs/next/components/modeler/feel/language-guide/feel-expressions-introduction/). It describes how to write FEEL expressions (e.g. data types, language constructs, builtin-functions, etc.). Or, check out examples and learning resources [here](https://camunda.github.io/feel-scala/docs/learn/).

Want to try it out? Use the [Playground](https://camunda.github.io/feel-scala/docs/playground/) to evaluate FEEL expressions. 

## Install

Please have a look at the [developer documentation](https://camunda.github.io/feel-scala/docs/reference/developer-guide/developer-guide-introduction). It describes how to integrate the engine into your application, and how to extend/customize it.

## Contribution

Contributions are welcome 🎉 Please have a look at the [Contribution Guide](./CONTRIBUTING.md).

The following resources can help to understand some general concepts behind the implementation: 
* [Build your own Programming Language with Scala](https://www.lihaoyi.com/post/BuildyourownProgrammingLanguagewithScala.html)
* [Easy Parsing with Parser Combinators](https://www.lihaoyi.com/post/EasyParsingwithParserCombinators.html)
* [FastParse - Documentation of the Parser Library](https://com-lihaoyi.github.io/fastparse/)

## License

[Apache License, Version 2.0](./LICENSE)
