---
id: feel-expressions-introduction
title: Introduction
---

FEEL expressions are powerful and can be used for various cases.

For a better overview, this section is split into expressions based on their operational data type:

* [Boolean](./feel-boolean-expressions.md)
* [String](./feel-string-expressions.md)
* [Numeric](./feel-numeric-expressions.md)
* [List](./feel-list-expressions.md)
* [Context](./feel-context-expressions.md)
* [Temporal](./feel-temporal-expressions.md)

The following sections cover more general areas that are not restricted to one data type:

* [Variables](./feel-variables.md)
* [Control Flow](./feel-control-flow.md)
* [Functions](./feel-functions.md)

### Comments

An Expression can contain comments to explain it and give it more context. This can be done using
Java-style comments. i.e. `//` to the end of line and `/*.... */`.

```js
// This should return 4
[1,2,3,4][-1]                                  

/* This should return 4 */
[1,2,3,4][-1]                                  
```