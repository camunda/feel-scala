---
id: feel-built-in-functions-introduction
title: Introduction
description: "FEEL includes many built-in functions. These functions can be invoked
in expressions and unary-tests."
---

FEEL includes many built-in functions. These functions can be invoked
in [expressions](../language-guide/feel-expressions-introduction.md)
and [unary-tests](../language-guide/feel-unary-tests.md).

```js
contains("me@camunda.com", ".com")
// invoke function with positional arguments

contains(string: "me@camunda.com", match: ".de")
// invoke function with named arguments
```

Read more about functions [here](../language-guide/feel-functions.md#invocation).

This section is split into functions based on their primary operational data type:

- [Boolean](./feel-built-in-functions-boolean.md)
- [String](./feel-built-in-functions-string.md)
- [Numeric](./feel-built-in-functions-numeric.md)
- [List](./feel-built-in-functions-list.md)
- [Context](./feel-built-in-functions-context.md)
- [Temporal](./feel-built-in-functions-temporal.md)
- [Range](./feel-built-in-functions-range.md)

Additionally, there are [conversion](./feel-built-in-functions-conversion.md) functions that allow
you to construct new values of a data type (factory functions).
