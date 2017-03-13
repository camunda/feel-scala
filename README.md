# FEEL Scala

A community extension for Camunda BPM which replaces the built-in FEEL engine of the Camunda DMN engine.

**Features:**

* full support for unary tests and expressions
* support all data types
* include built-in functions
* extensible by own functions

## What is FEEL?

FEEL (Friendly Enough Expression Language) is a part of the [DMN specification](http://www.omg.org/spec/DMN/) of the OMG. It is designed to write expressions for decision tables and literal expressions in a simple way what can easily understand by business professionals and developers.

## How to use it?

You can integrate the FEEL engine in different ways 

* [standalone or as script engine](https://github.com/camunda/feel-scala/tree/master/feel-engine#how-to-use-it)
* [together with a standalone / embedded Camunda DMN engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-factory#how-to-use-it) via feel engine factory spi
* [together with a Camunda BPM engine](https://github.com/camunda/feel-scala/tree/master/feel-engine-plugin#how-to-use-it) as process engine plugin

Then, you can use FEEL expressions in decision tables and decision literal expressions.

## Examples

### Unary Tests

```
< 7                                                 // input less than 7

not(2,4)                                            // input is not 2 or 4

[date("2015-09-17")..date("2015-09-19")]            // input is between '2015-09-17' and '2015-09-19'

<= duration("P1D")                                  // input is less or equal one day    
```

### Expression

```
applicant.monthly.income * 12                                           

if applicant.maritalStatus in ("M","S") then "valid" else "not valid"    

sum( [applicant.monthly.repayments, applicant.monthly.expenses] )        

sum( credit_history[record_date > date("2011-01-01")].weight )           

some ch in credit_history satisfies ch.event = "bankruptcy"      
```

See more [examples](https://github.com/camunda/feel-scala/tree/master/examples).

## Contribution

Found a bug? Please report it using [Github Issues](https://github.com/camunda/feel-scala/issues).

Want to extend, improve or fix a bug in the extension? [Pull Requests](https://github.com/camunda/feel-scala/pulls) are very welcome.

Want to discuss something? The [Camunda Forum](https://forum.camunda.org/c/community-extensions) might be the best place for it.

## License

[Apache License, Version 2.0](./LICENSE)
