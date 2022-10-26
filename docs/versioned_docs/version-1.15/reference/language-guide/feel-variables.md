---
id: feel-variables
title: Variables
description: "This document outlines variables and examples."
---

### Access variables

Access the value of a variable by its variable name.

```js
a + b
```

If the value of the variable is a context, a [context entry can be accessed](feel-context-expressions#get-entrypath) by its key. 

```js
a.b
```

:::tip

Use a [null-check](feel-boolean-expressions#null-check) if the variable can be `null` or is optional.

```js
a != null and a.b > 10 
```

:::

### Variable names

The name of a variable can be any alphanumeric string including the `_` symbol. For a combination of
words, it's recommended to use the `camelCase` or the `snake_case` format. The `kebab-case` format
is not allowed because it contains the operator `-`.

When accessing a variable in an expression, keep in mind the variable name is case-sensitive.

Restrictions of a variable name:

- It may not start with a *number* (e.g. `1stChoice` is not allowed; you can
  use `firstChoice` instead).
- It may not contain *whitespaces* (e.g. `order number` is not allowed; you can use `orderNumber`
  instead).
- It may not contain an *operator* (e.g. `+`, `-`, `*`, `/`, `=`, `>`, `<`, `?`, `.`).
- It may not be a *literal* (e.g. `null`, `true`, `false`) or a *keyword* (e.g. `function`, `if`
  , `then`, `else`, `for`, `return`, `between`, `instance`, `of`, `not`, `in`, `and`, `or`, `some`,
  `every`, `satisfies`).

### Escape variable names

If a variable name or a context key contains any special character (e.g. whitespace, dash, etc.)
then the name can be wrapped into single backquotes/backticks (e.g. ``` `foo bar` ```).

```js
`first name`

`tracking-id`

order.`total price`
```

:::tip
Use the [`get value()`](../builtin-functions/feel-built-in-functions-context.md#get-value) function
to retrieve the context value of an arbitrary key.

```js
get value(order, "total price")
```
:::
