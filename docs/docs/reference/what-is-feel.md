---
id: what-is-feel
title: What is FEEL?
description: "FEEL is a part of DMN specification of the Object Management Group."
---

FEEL (Friendly Enough Expression Language) is a part of
the [DMN specification](http://www.omg.org/spec/DMN/) of the Object Management Group (OMG). It is designed to write expressions for decision tables and literal expressions in a way that is easily understood by business professionals and developers.

## Unary-tests vs. expressions

FEEL has two types of expressions for different use cases:

### Unary-tests

A [unary-tests expression](./language-guide/feel-unary-tests.md) is a special kind of boolean expression. It should be used for the input
entries of a decision table (i.e. the conditions of a rule).

```js
< 7
// checks if the input value is less than 7

not(2,4)
// checks if the input value is neither 2 nor 4

[date("2015-09-17")..date("2015-09-19")]
// checks if the input value is between '2015-09-17' and '2015-09-19'

<= duration("P1D")
// checks if the input value is less than or equal to one day
```

### Expressions

[General expressions](./language-guide/feel-expressions-introduction.md) that can return values of different types. They can be used everywhere; for
example, in a decision table as an input expression or as an output entry.

```js
applicant.monthly.income * 12

if applicant.maritalStatus in ("M","S") then "valid" else "not valid"

sum( [applicant.monthly.repayments, applicant.monthly.expenses] )

sum( credit_history[record_date > date("2011-01-01")].weight )

some ch in credit_history satisfies ch.event = "bankruptcy"
```
