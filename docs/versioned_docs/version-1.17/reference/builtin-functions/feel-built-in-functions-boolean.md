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

Checks if a given value is not `null`. If the value is `null` then the function returns `false`. 
Otherwise, the function returns `true`.

**Function signature**

```js
is defined(value: Any): boolean
```

**Examples**

```js
is defined(1)
// true

is defined(null)
// false

is defined(x)
// false - if no variable "x" exists

is defined(x.y)
// false - if no variable "x" exists or it doesn't have a property "y"
```

:::caution Breaking change

This function worked differently in previous versions. It returned `true` if the value was `null`.
Since this version, the function returns `false` if the value is `null`. 

:::

## get or else(value, default)

<MarkerCamundaExtension></MarkerCamundaExtension>

Return the provided value parameter if not `null`, otherwise return the default parameter

**Function signature**

```js
get or else(value: Any, default: Any): Any
```

**Examples**

```js
get or else("this", "default")
// "this"

get or else(null, "default")
// "default"

get or else(null, null)     
// null
```

## assert(value, condition)

<MarkerCamundaExtension></MarkerCamundaExtension>

Verify that the given condition is met. If the condition is `true`, the function returns the value. 
Otherwise, the evaluation fails with an error.

**Function signature**

```js
assert(value: Any, condition: Any)
```

**Examples**

```js
assert(x, x != null)
// "value" - if x is "value"
// error - if x is null or doesn't exist

assert(x, x >= 0) 
// 4 - if x is 4
// error - if x is less than zero
```

## assert(value, condition, cause)

<MarkerCamundaExtension></MarkerCamundaExtension>

Verify that the given condition is met. If the condition is `true`, the function returns the value.
Otherwise, the evaluation fails with an error containing the given message.

**Function signature**

```js
assert(value: Any, condition: Any, cause: String)
```

**Examples**

```js
assert(x, x != null, "'x' should not be null")
// "value" - if x is "value"
// error('x' should not be null) - if x is null or doesn't exist

assert(x, x >= 0, "'x' should be positive")
// 4 - if x is 4
// error('x' should be positive) - if x is less than zero
```
