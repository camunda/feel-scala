---
id: feel-built-in-functions-boolean
title: Boolean Functions
---

## not()

* parameters:
  * `negand`: boolean
* result: boolean

```js
not(true)
// false
```

## is defined()

Checks if a given value is defined or not. A value is defined if it exists, and it is an instance of one of the FEEL data types including `null`.

The function can be used to check if a variable, or a context entry (e.g. a property of a variable) exists. It allows differentiating between a variable that is `null` and a value that doesn't exist.   

* parameters:
  * `value`: any
* result: boolean

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
