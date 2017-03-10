# FEEL Scala

A community extension for Camunda BPM which replaces the built-in FEEL engine of the Camunda DMN engine.

**Features:**

* comprehensive support for unary tests and expressions
* built-in functions
* extensible by own functions

## What is FEEL?

FEEL (Friendly Enough Expression Language) is a part of the [DMN specification](http://www.omg.org/spec/DMN/) of the OMG. It is designed to write expressions for decision tables and literal expressions in a simple way what can easily understand by business professionals and developers.

## How to use it?

You can use the FEEL engine in different ways 

* [standalone or as script engine](https://github.com/camunda/feel-scala/tree/master/feel-engine#how-to-use-it)
* [together with a standalone / embedded Camunda DMN engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-factory#how-to-use-it) via feel engine factory spi
* [together with a Camunda BPM engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-plugin#how-to-use-it) as process engine plugin

## Examples

Just to have a feeling how an expression can look like:

### Simple Unary Test
```
< 42                                        // input less than 42

(2..4)                                      // input greater than 2 and less than 4

2,4                                         // input is 2 or 4

not(2,4)                                    // input is not 2 or 4

>= a                                        // input is greater or equal to the value of variable 'a'

"good"                                      // input is equal the string 'good'

[date("2015-09-17")..date("2015-09-19")]    // input is after or equal '2015-09-17' and before or equal '2015-09-19'

]time("08:00:00")..time("16:00:00")[        // input is after '08:00:00' and before '16:00:00'

<= duration("P1D")                          // input is less or equal to 'P1D' (one day)

```

## Contribution

Found a bug? Please report it using [Github Issues](https://github.com/camunda/feel-scala/issues).

Want to extend, improve or fix a bug in the extension? [Pull Requests](https://github.com/camunda/feel-scala/pulls) are very welcome.

Want to discuss something? The [Camunda Forum](https://forum.camunda.org/c/community-extensions) might be the best place for it.

## License

[Apache License, Version 2.0](./LICENSE)
