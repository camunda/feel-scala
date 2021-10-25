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
Java-style comments: `//` to the end of line, or `/*.... */` for blocks.

```js
// returns the last item       
[1,2,3,4][-1]                             
    
/* returns the last item */
[1,2,3,4][-1]

/* 
 * returns the last item 
 */
[1,2,3,4][-1]   
```

### Parentheses

Parentheses `( .. )` can be used in expressions as a way to separate different parts of an
expression. Or, to influence the precedence of the operators.

```js
(5 - 3) * (4 / 2)

x < 5 and (y > 10 or z > 20)

if (5 < 10) then "low" else "high"
```  
