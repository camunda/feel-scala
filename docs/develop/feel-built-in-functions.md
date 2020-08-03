---
title: FEEL Language Reference: Built-in Functions
---

# Built-in Functions

The following functions are available and can be used in expressions and unary-tests.

Note that some function/parameter names contain whitespaces. However, they can be invoked directly - without escaping the name.  

## Conversion Functions 

Convert a value into a different type.

### date()

* parameters:
  * `from`: string / date-time
  * or `year`, `month`, `day`: number 
* result: date

```js
date(birthday) 
// date("2018-04-29")

date(date and time("2012-12-25T11:00:00"))
// date("2012-12-25")

date(2012, 12, 25)
// date("2012-12-25")
```

### time()

* parameters:
  * `from`: string / date-time
  * or `hour`, `minute`, `second`: number 
    * (optional) `offset`: day-time-duration
* result: time

```js
time(lunchTime) 
// time("12:00:00")

time(date and time("2012-12-25T11:00:00"))
// time("11:00:00")

time(23, 59, 0)
// time("23:59:00")

time(14, 30, 0, duration("PT1H"))
// time("15:30:00")
```

### date and time()

* parameters:
  * `date`: date / date-time
  * `time`: time
  * or `from`: string 
* result: date-time

```js
date and time(date("2012-12-24"),time("T23:59:00")) 
// date and time("2012-12-24T23:59:00")

date and time(date and time("2012-12-25T11:00:00"),time("T23:59:00"))
// date and time("2012-12-25T23:59:00")

date and time(birthday) 
// date and time("2018-04-29T009:30:00")
```

### duration()

* parameters:
  * `from`: string
* result: day-time-duration or year-month-duration

```js
duration(weekDays)
// duration("P5D")

duration(age)
// duration("P32Y")
```

### years and months duration()

* parameters:
  * `from`: date
  * `to`: date
* result: year-month-duration

```js
years and months duration(date("2011-12-22"), date("2013-08-24"))
// duration("P1Y8M")
```

### number()

* parameters:
  * `from`: string
* result: number

```js
number("1500.5") 
// 1500.5
```

### string()

* parameters:
  * `from`: any
* result: string

```js
string(1.1) 
// "1.1"

string(date("2012-12-25"))
// "2012-12-25"
```

## Boolean Functions 

### not()

* parameters:
  * `negand`: boolean
* result: boolean

```js
not(true)
// false
```

## String Functions 

### substring()

* parameters:
  * `string`: string
  * `start position`: number
  * (optional) `length`: number  
* result: string

```js
substring("foobar",3) 
// "obar"

substring("foobar",3,3) 
// "oba"
```

### string length()

* parameters:
  * `string`: string
* result: number

```js
string length("foo") 
// 3
```

### upper case()

* parameters:
  * `string`: string
* result: string

```js
upper case("aBc4") 
// "ABC4"
```

### lower case()

* parameters:
  * `string`: string
* result: string

```js
lower case("aBc4") 
// "abc4"
```

### substring before()

* parameters:
  * `string`: string
  * `match`: string
* result: string

```js
substring before("foobar", "bar") 
// "foo"
```

### substring after()

* parameters:
  * `string`: string
  * `match`: string
* result: string

```js
substring after("foobar", "ob") 
// "ar"
```

### contains()

* parameters:
  * `string`: string
  * `match`: string
* result: boolean

```js
contains("foobar", "of") 
// false
```

### starts with()

* parameters:
  * `input`: string
  * `match`: string
* result: boolean

```js
starts with("foobar", "fo") 
// true
```

### ends with()

* parameters:
  * `input`: string
  * `match`: string
* result: boolean

```js
ends with("foobar", "r") 
// true
```

### matches()

* parameters:
  * `input`: string
  * `pattern`: string (regular expression)
* result: boolean

```js
matches("foobar", "^fo*bar") 
// true
```

### replace()

* parameters:
  * `input`: string
  * `pattern`: string (regular expression)
  * `replacement`: string (e.g. `$1` returns the first match group) 
  * (optional) `flags`: string ("s", "m", "i", "x")
* result: string

```js
replace("abcd", "(ab)|(a)", "[1=$1][2=$2]")
// "[1=ab][2=]cd"

replace("0123456789", "(\d{3})(\d{3})(\d{4})", "($1) $2-$3")
// "(012) 345-6789"
```

### split()

* parameters:
  * `string`: string
  * `delimiter`: string (regular expression)
* result: list of strings

```js
split("John Doe", "\s" ) 
// ["John", "Doe"]

split("a;b;c;;", ";")
// ["a", "b", "c", "", ""]
```

## List Functions 

### list contains()

* parameters:
  * `list`: list
  * `element`: any
* result: boolean

```js
list contains([1,2,3], 2) 
// true
```

### count()

* parameters:
  * `list`: list
* result: number

```js
count([1,2,3]) 
// 3
```

### min()

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

### max()

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

### sum()

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

### product()

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

### mean()

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

### median()

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

### stddev()

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

### mode()

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

### and() / all()

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

### or() / any()

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

### sublist()

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

### append()

* parameters:
  * `list`: list
  * `items`: elements as varargs
* result: list

```js
append([1], 2, 3)
// [1,2,3]
```

### concatenate()

* parameters:
  * `lists`: lists as varargs
* result: list

```js
concatenate([1,2],[3]) 
// [1,2,3]

concatenate([1],[2],[3])
// [1,2,3]
```

### insert before()

* parameters:
  * `list`: list
  * `position`: number
  * `newItem`: any
* result: list

```js
insert before([1,3],1,2) 
// [1,2,3]
```

### remove()

* parameters:
  * `list`: list
  * `position`: number
* result: list

```js
remove([1,2,3], 2) 
// [1,3]
```

### reverse()

* parameters:
  * `list`: list
* result: list

```js
reverse([1,2,3]) 
// [3,2,1]
```

### index of()

* parameters:
  * `list`: list
  * `match`: any
* result: list of numbers

```js
index of([1,2,3,2],2) 
// [2,4]
```

### union()

* parameters:
  * `lists`: lists as varargs
* result: list

```js
union([1,2],[2,3])
// [1,2,3]
```

### distinct values()

* parameters:
  * `list`: list
* result: list

```js
distinct values([1,2,3,2,1])
// [1,2,3]
```

### flatten()

* parameters:
  * `list`: list
* result: list

```js
flatten([[1,2],[[3]], 4])
// [1,2,3,4]
```

### sort()

* parameters:
  * `list`: list 
  * `precedes`: function with two arguments and boolean result
* result: list

```js
sort(list: [3,1,4,5,2], precedes: function(x,y) x < y) 
// [1,2,3,4,5]
```

## Numeric Functions 

### decimal()

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

### floor()

* parameters:
  * `n`: number
* result: number

```js
floor(1.5)
// 1

floor(-1.5)
// -2
```

### ceiling()

* parameters:
  * `n`: number
* result: number

```js
ceiling(1.5)
// 2

floor(-1.5)
// -1
```

### abs()

* parameters:
  * `number`: number
* result: number

```js
abs(10)
// 10

abs(-10)
// 10
```

### modulo()

Returns the remainder of the division of dividend by divisor.

* parameters:
  * `dividend`: number
  * `divisor`: number
* result: number

```js
modulo(12, 5)
// 2
```

### sqrt()

Returns the square root.

* parameters:
  * `number`: number
* result: number

```js
sqrt(16)
// 4
```

### log()

Returns the natural logarithm (base e) of the number.

* parameters:
  * `number`: number
* result: number

```js
log(10)
// 2.302585092994046
```

### exp()

Returns the Euler’s number e raised to the power of number .

* parameters:
  * `number`: number
* result: number

```js
exp(5)
// 148.4131591025766
```

### odd()

* parameters:
  * `number`: number
* result: boolean

```js
odd(5)
// true
```

### even()

* parameters:
  * `number`: number
* result: boolean

```js
odd(5)
// false
```

## Context Functions 

### get value()

Returns the value of the context entry with the given key.

* parameters:
  * `context`: context
  * `key`: string
* result: any

```js
get value({foo: 123}, "foo") 
// 123
```

### get entries()

Returns the entries of the context as list of key-value-pairs.

* parameters:
  * `context`: context
* result: list of context which contains two entries for "key" and "value"

```js
get entries({foo: 123})
// [{key: "foo", value: 123}]
```
