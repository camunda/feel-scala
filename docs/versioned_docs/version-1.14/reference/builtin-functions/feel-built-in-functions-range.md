---
id: feel-built-in-functions-range
title: Range Functions
---

## before

* parameters:
  * `range`: range
  * `point`: number
  * `range1`: range
  * `range2`: range
  * `point1`: number
  * `point2`: number
* result: boolean

```js
before(1, 10)
// true

before(10, 1)
// false

before(1, [2..5])
// true

before([1..5], 10)
// true

before([1..5], [6..10])
// true

before([1..5),[5..10])
// true
```


## after

* parameters:
  * `range`: range
  * `point`: number
  * `range1`: range
  * `range2`: range
  * `point1`: number
  * `point2`: number
* result: boolean


## meets

## met by

## overlaps

## overlaps before

## overlaps after

## finishes

## finished by

## includes

## during

## start

## started by

## coincides
