---
id: feel-built-in-functions-conversion
title: Conversion Functions
---

Convert a value into a different type.

## date()

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

## time()

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

## date and time()

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

## duration()

* parameters:
  * `from`: string
* result: day-time-duration or year-month-duration

```js
duration(weekDays)
// duration("P5D")

duration(age)
// duration("P32Y")
```

## years and months duration()

* parameters:
  * `from`: date
  * `to`: date
* result: year-month-duration

```js
years and months duration(date("2011-12-22"), date("2013-08-24"))
// duration("P1Y8M")
```

## number()

* parameters:
  * `from`: string
* result: number

```js
number("1500.5") 
// 1500.5
```

## string()

* parameters:
  * `from`: any
* result: string

```js
string(1.1) 
// "1.1"

string(date("2012-12-25"))
// "2012-12-25"
```
