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
