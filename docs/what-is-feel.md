---
title: What is FEEL?
---

## What is FEEL?

FEEL (Friendly Enough Expression Language) is a part of the [DMN specification](http://www.omg.org/spec/DMN/) of the OMG. It is designed to write expressions for decision tables and literal expressions in a simple way what can easily understand by business professionals and developers.

## Unary Tests vs. Expression

FEEL has two entry points: unary-tests and expressions. 

### Unary Tests

Unary-Tests can be used only for input entries of a decision table. They are a special kind of expression with a different grammar. The expression gets the value of the input expression implicitly as the first argument. The result of the expression must be either `true` or `false`.

Examples:

```js
< 7                                                 
// input less than 7

not(2,4)                                            
// input is not 2 or 4

[date("2015-09-17")..date("2015-09-19")]            
// input is between '2015-09-17' and '2015-09-19'

<= duration("P1D")                                  
// input is less or equal one day    
```

### Expression

Expressions can be used everywhere, e.g. in a decision table as input expression or output entry. An expression takes no implicit arguments like unary-tests.

Examples:

```js
applicant.monthly.income * 12                                           

if applicant.maritalStatus in ("M","S") then "valid" else "not valid"    

sum( [applicant.monthly.repayments, applicant.monthly.expenses] )        

sum( credit_history[record_date > date("2011-01-01")].weight )           

some ch in credit_history satisfies ch.event = "bankruptcy"      
```

## Next

* [language reference](https://camunda.github.io/feel-scala/language-reference)
* [more examples](https://github.com/camunda/feel-scala/tree/master/examples)
