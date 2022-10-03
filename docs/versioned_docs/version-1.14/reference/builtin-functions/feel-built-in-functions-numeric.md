---
id: feel-built-in-functions-numeric
title: Numeric Functions
---

## decimal()

Round the given number at the given scale using the given rounding mode. If no rounding mode is passed in then it uses `HALF_EVEN` as default. 

* parameters:
  * `n`: number
  * `scale`: number
  * (optional) `mode`: string - one of `UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN, UNNECESSARY` (default: `HALF_EVEN`)
* result: number

```js
decimal(1/3, 2)
// .33

decimal(1.5, 0) 
// 2

decimal(2.5, 0, "half_up")
// 3
```

## floor()

* parameters:
  * `n`: number
* result: number

```js
floor(1.5)
// 1

floor(-1.5)
// -2
```

## ceiling()

* parameters:
  * `n`: number
* result: number

```js
ceiling(1.5)
// 2

floor(-1.5)
// -1
```

## abs()

* parameters:
  * `number`: number
* result: number

```js
abs(10)
// 10

abs(-10)
// 10
```

## modulo()

Returns the remainder of the division of dividend by divisor.

* parameters:
  * `dividend`: number
  * `divisor`: number
* result: number

```js
modulo(12, 5)
// 2
```

## sqrt()

Returns the square root.

* parameters:
  * `number`: number
* result: number

```js
sqrt(16)
// 4
```

## log()

Returns the natural logarithm (base e) of the number.

* parameters:
  * `number`: number
* result: number

```js
log(10)
// 2.302585092994046
```

## exp()

Returns the Euler’s number e raised to the power of number .

* parameters:
  * `number`: number
* result: number

```js
exp(5)
// 148.4131591025766
```

## odd()

* parameters:
  * `number`: number
* result: boolean

```js
odd(5)
// true
```

## even()

* parameters:
  * `number`: number
* result: boolean

```js
odd(5)
// false
```
