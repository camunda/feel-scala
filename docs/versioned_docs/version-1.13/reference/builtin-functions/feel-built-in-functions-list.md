---
id: feel-built-in-functions-list
title: List Functions
---

## list contains()

* parameters:
  * `list`: list
  * `element`: any
* result: boolean

```js
list contains([1,2,3], 2) 
// true
```

## count()

* parameters:
  * `list`: list
* result: number

```js
count([1,2,3]) 
// 3
```

## min()

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
min([1,2,3]) 
// 1

min(1,2,3) 
// 1
```

## max()

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
min([1,2,3]) 
// 3

min(1,2,3) 
// 3
```

## sum()

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
min([1,2,3]) 
// 6

min(1,2,3) 
// 6
```

## product()

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
product([2, 3, 4])
// 24

product(2, 3, 4)
// 24
```

## mean()

Returns the arithmetic mean (i.e. average).

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
mean([1,2,3])
// 2

mean(1,2,3)
// 2
```

## median()

Returns the median element of the list of numbers.

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
median(8, 2, 5, 3, 4)
// 4

median([6, 1, 2, 3]) 
// 2.5
```

## stddev()

Returns the standard deviation.

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: number

```js
stddev(2, 4, 7, 5)
// 2.0816659994661326

stddev([2, 4, 7, 5])
// 2.0816659994661326
```

## mode()

Returns the mode of the list of numbers.

* parameters:
  * `list`: list of numbers
  * or numbers as varargs
* result: list of numbers

```js
mode(6, 3, 9, 6, 6) 
// [6]

mode([6, 1, 9, 6, 1]) 
// [1, 6]
```

## and() / all()

* parameters:
  * `list`: list of booleans
  * or booleans as varargs
* result: boolean

```js
and([true,false])
// false

and(false,null,true)
// false
```

## or() / any()

* parameters:
  * `list`: list of booleans
  * or booleans as varargs
* result: boolean

```js
or([false,true])
// true

or(false,null,true)
// true
```

## sublist()

* parameters:
  * `list`: list
  * `start position`: number
  * (optional) `length`: number
* result: list

```js
sublist([1,2,3], 2)
// [2,3]

sublist([1,2,3], 1, 2)
// [1,2]
```

## append()

* parameters:
  * `list`: list
  * `items`: elements as varargs
* result: list

```js
append([1], 2, 3)
// [1,2,3]
```

## concatenate()

* parameters:
  * `lists`: lists as varargs
* result: list

```js
concatenate([1,2],[3]) 
// [1,2,3]

concatenate([1],[2],[3])
// [1,2,3]
```

## insert before()

* parameters:
  * `list`: list
  * `position`: number
  * `newItem`: any
* result: list

```js
insert before([1,3],1,2) 
// [1,2,3]
```

## remove()

* parameters:
  * `list`: list
  * `position`: number
* result: list

```js
remove([1,2,3], 2) 
// [1,3]
```

## reverse()

* parameters:
  * `list`: list
* result: list

```js
reverse([1,2,3]) 
// [3,2,1]
```

## index of()

* parameters:
  * `list`: list
  * `match`: any
* result: list of numbers

```js
index of([1,2,3,2],2) 
// [2,4]
```

## union()

* parameters:
  * `lists`: lists as varargs
* result: list

```js
union([1,2],[2,3])
// [1,2,3]
```

## distinct values()

* parameters:
  * `list`: list
* result: list

```js
distinct values([1,2,3,2,1])
// [1,2,3]
```

## flatten()

* parameters:
  * `list`: list
* result: list

```js
flatten([[1,2],[[3]], 4])
// [1,2,3,4]
```

## sort()

* parameters:
  * `list`: list 
  * `precedes`: function with two arguments and boolean result
* result: list

```js
sort(list: [3,1,4,5,2], precedes: function(x,y) x < y) 
// [1,2,3,4,5]
```
