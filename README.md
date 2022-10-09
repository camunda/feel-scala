# FEEL Scala

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.camunda.feel/feel-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.camunda.feel/feel-engine)

A parser and interpreter for FEEL that is written in Scala (see [What is FEEL?](https://camunda.github.io/feel-scala/docs/reference/what-is-feel)).

The FEEL engine started as a slack time project, grown to a community-driven project, and is now officially maintained by [Camunda](https://camunda.org/) :rocket: 

It is integrated with the following projects:
* [Camunda Platform](https://docs.camunda.org/manual/user-guide/dmn-engine/feel/) as part of the DMN engine
* [Zeebe](https://docs.camunda.io/docs/product-manuals/concepts/expressions#the-expression-language) as expression language

**Features:** :sparkles:

* full support for unary tests and expressions (DMN 1.2)
* including built-in functions
* extensible by own functions and custom object mappers

## Usage 

Please have a look at the [documentation](https://camunda.github.io/feel-scala/docs/reference). It describes how to write FEEL expressions (e.g. data types, language constructs, builtin-functions, etc.), and contains some examples.

## Install

Please have a look at the [documentation](https://camunda.github.io/feel-scala/docs/reference/developer-guide/developer-guide-introduction). It describes how to integrate the engine into your application, and how to extend/customize it.

## Contribution

Contributions are welcome 🎉 Please have a look at the [Contribution Guide](./CONTRIBUTING.md).

The following resources can help to understand some general concepts behind the implementation: 
* [Build your Programming Language with Scala](https://www.lihaoyi.com/post/BuildyourownProgrammingLanguagewithScala.html)
* [Easy Parsing with Parser Combinators](https://www.lihaoyi.com/post/EasyParsingwithParserCombinators.html)
* [FastParse - Documentation of the Parser Library](https://com-lihaoyi.github.io/fastparse/)

## License

[Apache License, Version 2.0](./LICENSE)
