---
id: feel-built-in-functions-boolean
title: Boolean functions
description: "This document outlines current boolean functions and a few examples."
---

import MarkerCamundaExtension from "@site/src/components/MarkerCamundaExtension";

## not(negand)

Returns the logical negation of the given value.

**Function signature**

```js
not(negand: boolean): boolean
```

**Examples**

```js
not(true)
// false

not(null) 
// null
```

## is defined(value)

<MarkerCamundaExtension></MarkerCamundaExtension>

Checks if a given value is defined. A value is defined if it exists, and it is an instance of one of the FEEL data types including `null`.

The function can be used to check if a variable or a context entry (e.g. a property of a variable) exists. It allows differentiating between a `null` variable and a value that doesn't exist.

**Function signature**

```js
is defined(value: Any): boolean
```

**Examples**

```js
is defined(1)
// true

is defined(null)
// true

is defined(x)
// false - if no variable "x" exists

is defined(x.y)
// false - if no variable "x" exists or it doesn't have a property "y"
```

## get or else(value, default)

<MarkerCamundaExtension></MarkerCamundaExtension>

Return the provided value parameter if not `null`, otherwise return the default parameter

**Function signature**

```js
get or else(value: Any, default: Any): Any
```

**Examples**

```js
get or default("this", "default")
// "this"

get or default(null, "default")
// "default"

get or default(null, null)     
// null
```

## assert(value, condition)

<MarkerCamundaExtension></MarkerCamundaExtension>

Verify that the provided condition is met, if the condition is true the function returns the value. Otherwise, the evaluation fails with an error

**Function signature**

```js
assert(value: Any, condition: Any)
```

**Examples**

```js
assert(x, x > 3) with x = 4
// 4

assert(x, x != null) with x = "value"
// "value"

assert(x, x > 5) with x = 4
// error("The condition is not fulfilled")
```

## assert(value, condition, cause)

<MarkerCamundaExtension></MarkerCamundaExtension>

Verify that the provided condition is met, if the condition is true the function returns the value. Otherwise, the evaluation fails with an error and the provide error message

**Function signature**

```js
assert(value: Any, condition: Any, cause: String)
```

**Examples**

```js
assert(x, x > 3, "Custom error message") with x = 4
// 4

assert(x, x > 5, "Custom error message") with x = 4
// error("Custom error message")

assert(x, x != null, "Custom error message") with x = null
// error("Custom error message")
```
