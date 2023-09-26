---
id: feel-functions
title: Functions
description: "This document outlines various functions and examples."
---

### Invocation

Invokes a built-in function (
e.g. [contains()](../builtin-functions/feel-built-in-functions-string#containsstring-match)) or a user-defined
function by its name. The arguments of the function can be passed positional or named.

- Positional: Only the values, in the same order as defined by the function (e.g. `f(1,2)`).
- Named: The values with the argument name as prefix, in any order (e.g. `f(a: 1, b: 2)`).

```js
contains("me@camunda.com", ".com")
// true

contains(string: "me@camunda.com", match: ".de")
// false
```

:::info GOOD TO KNOW

The invocation returns `null` if the no function exists with the given name, or if the argument
types don't match the function signature.

:::

### User-defined

```js
function(a,b) e
```

Defines a function with a list of argument names, and an expression (i.e. the function body). When
the function is invoked, it assigns the values to the arguments and evaluates the expression.

Within an expression, a function can be defined and invoked in a context.

```js
{
  age: function(birthday) (today() - birthday).years
}
```

### External

Defines a function that calls a static Java method. The definition must include the full qualified
class name and the method signature.

```js
function(x,y) external { 
    java: { 
        class: "java.lang.Math", 
        method signature: "max(int, int)" 
    } 
}
```

:::danger Security 
External functions are disabled by default. They would allow calling arbitrary
code or accessing sensitive data. It is recommended to use the 
[FunctionProvider API](../developer-guide/function-provider-spi.md) instead.
:::