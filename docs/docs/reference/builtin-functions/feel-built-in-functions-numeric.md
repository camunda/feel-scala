---
id: feel-built-in-functions-numeric
title: Numeric functions
description: "This document outlines built-in numeric functions and examples."
---

import MarkerCamundaExtension from "@site/src/components/MarkerCamundaExtension";

## decimal(n, scale)

Rounds the given value at the given scale.

**Function signature**

```js
decimal(n: number, scale: number): number
```

**Examples**

```js
decimal(1/3, 2)
// .33

decimal(1.5, 0) 
// 2
```

## floor(n)

Rounds the given value with rounding mode flooring.

**Function signature**

```js
floor(n: number): number
```

**Examples**

```js
floor(1.5)
// 1

floor(-1.5)
// -2
```

## floor(n, scale)

Rounds the given value with rounding mode flooring at the given scale.

**Function signature**

```js
floor(n: number, scale: number): number
```

**Examples**

```js
floor(-1.56, 1)
// -1.6
```

## ceiling(n)

Rounds the given value with rounding mode ceiling.

**Function signature**

```js
ceiling(n: number): number
```

**Examples**

```js
ceiling(1.5)
// 2

ceiling(-1.5)
// -1
```

## ceiling(n, scale)

Rounds the given value with rounding mode ceiling at the given scale.

**Function signature**

```js
ceiling(n: number, scale: number): number
```

**Examples**

```js
ceiling(-1.56, 1)
// -1.5
```

## round up(n, scale)

Rounds the given value with the rounding mode round-up at the given scale.

**Function signature**

```js
round up(n: number, scale: number): number
```

**Examples**

```js
round up(5.5, 0) 
// 6

round up(-5.5, 0)
// -6

round up(1.121, 2)
// 1.13

round up(-1.126, 2)
// -1.13
```

## round down(n, scale)

Rounds the given value with the rounding mode round-down at the given scale.

**Function signature**

```js
round down(n: number, scale: number): number
```

**Examples**

```js
round down(5.5, 0)
// 5

round down (-5.5, 0)
// -5

round down (1.121, 2)
// 1.12

round down (-1.126, 2)
// -1.12
```

## round half up(n, scale)

Rounds the given value with the rounding mode round-half-up at the given scale.

**Function signature**

```js
round half up(n: number, scale: number): number
```

**Examples**

```js
round half up(5.5, 0)
// 6

round half up(-5.5, 0)
// -6

round half up(1.121, 2) 
// 1.12

round half up(-1.126, 2)
// -1.13
```

## round half down(n, scale)

Rounds the given value with the rounding mode round-half-down at the given scale.

**Function signature**

```js
round half down(n: number, scale: number): number
```

**Examples**

```js
round half down (5.5, 0)
// 5

round half down (-5.5, 0)
// -5

round half down (1.121, 2)
// 1.12

round half down (-1.126, 2)
// -1.13
```

## abs(number)

Returns the absolute value of the given numeric value.

**Function signature**

```js
abs(number: number): number
```

**Examples**

```js
abs(10)
// 10

abs(-10)
// 10
```

## modulo(dividend, divisor)

Returns the remainder of the division of dividend by divisor.

**Function signature**

```js
modulo(dividend: number, divisor: number): number
```

**Examples**

```js
modulo(12, 5)
// 2
```

## sqrt(number)

Returns the square root of the given value.

**Function signature**

```js
sqrt(number: number): number
```

**Examples**

```js
sqrt(16)
// 4
```

## log(number)

Returns the natural logarithm (base e) of the given value.

**Function signature**

```js
log(number: number): number
```

**Examples**

```js
log(10)
// 2.302585092994046
```

## exp(number)

Returns the Euler’s number e raised to the power of the given number .

**Function signature**

```js
exp(number: number): number
```

**Examples**

```js
exp(5)
// 148.4131591025766
```

## odd(number)

Returns `true` if the given value is odd. Otherwise, returns `false`.

**Function signature**

```js
odd(number: number): boolean
```

**Examples**

```js
odd(5)
// true

odd(2)
// false
```

## even(number)

Returns `true` if the given is even. Otherwise, returns `false`.

**Function signature**

```js
even(number: number): boolean
```

**Examples**

```js
even(5)
// false

even(2)
// true
```

## random number()

<MarkerCamundaExtension></MarkerCamundaExtension>

Returns a random number between `0` and `1`. 

**Function signature**

```js
random number(): number
```

**Examples**

```js
random number()
// 0.9701618132579795
```