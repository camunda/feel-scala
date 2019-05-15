# FEEL Scala

A parser and interpreter for FEEL which is written in Scala (see [What is FEEL?](https://camunda.github.io/feel-scala/what-is-feel)).

It is desined as a community extension for Camunda BPM to replace the built-in FEEL engine of the [Camunda DMN engine](https://github.com/camunda/camunda-engine-dmn). However, it can also be used outside of Camunda BPM as native Scala application or script engine. 

**Features:**

* full support for unary-tests and expressions
* including built-in functions
* extensible by own functions and custom object mappers

## Install/Usage

The FEEL engine can be used in different ways 

* [standalone or as script engine](https://github.com/camunda/feel-scala/tree/master/feel-engine#how-to-use-it)
* [integrated into the Camunda DMN engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-factory#how-to-use-it) via feel engine factory SPI
* [integrated into the Camunda BPM engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-plugin#how-to-use-it) as process engine plugin 

## More Information

* [New Documentation](https://camunda.github.io/feel-scala/) (in progress)
* [Current Documentation](https://github.com/camunda/feel-scala/wiki) 

## Contribution

See the [Contribution Guide](./CONTRIBUTING.md).

## License

[Apache License, Version 2.0](./LICENSE)
