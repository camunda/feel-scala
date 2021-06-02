---
id: feel-expression
title: Expressions
---

### Path Expression

Access a value by its name/path. For example, a given variable from the input/context.

```js
x + y
```

If the name or path contains any special character (e.g. whitespace, dash, etc.) then the name needs to be wrapped into single backquotes/backtick `` `foo bar` ``.

```js
`name with whitespace`.`name+operator`
```

### Comparison
  
Any value can be compared with `null` to check if it is equal to `null`, or if it exists. Comparing `null` to a value different from `null` results in `false`. It returns `true` if the value, or the context entry (e.g. the property of a variable) is `null` or doesn't exist. The built-in function [is defined()](../builtin-functions/feel-built-in-functions-boolean.md#is-defined) can be used to differentiate between a value that is `null` and a value that doesn't exist. 

```js
null = null
// true

"foo" = null
// false

x = null
// true - if "x" is null or doesn't exist

x.y = null
// true - if "x" is null, "x" doesn't exist, 
//           "y" is null, or "x" has no property "y" 
```  
