---
id: feel-string-expressions 
title: String Expressions
---

### Literal

Creates a new string value.

```js
"valid"
```

### Addition / Concatenation

An addition concatenates the strings. The result is a string containing the characters of both strings.

```js
"foo" + "bar"
// "foobar"
```

:::tip Tip 

The concatenation is only available for string values. For other type, you can use
the [string()](../builtin-functions/feel-built-in-functions-conversion#string) function to convert
the value into a string first.

```js
"order-" + string(123)
```

:::