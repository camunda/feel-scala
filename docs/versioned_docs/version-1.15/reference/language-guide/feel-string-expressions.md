---
id: feel-string-expressions
title: String expressions
description: "This document outlines string expressions and examples."
---

### Literal

Creates a new string value.

```js
"valid"
```

### Addition/concatenation

An addition concatenates the strings. The result is a string containing the characters of both strings.

```js
"foo" + "bar"
// "foobar"
```

:::tip

The concatenation is only available for string values. For other types, you can use
the [string()](../builtin-functions/feel-built-in-functions-conversion#string) function to convert
the value into a string first.

```js
"order-" + string(123)
```

:::
