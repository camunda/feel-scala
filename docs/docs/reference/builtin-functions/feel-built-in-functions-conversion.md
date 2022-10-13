---
id: feel-built-in-functions-conversion
title: Conversion functions
description: "This document outlines conversion functions and a few examples."
---

Convert a value into a different type.

## date()

- parameters:
  - `from`: string / date-time
  - or `year`, `month`, `day`: number
- result: date

```js
date(birthday) 
// date("2018-04-29")

date(date and time("2012-12-25T11:00:00"))
// date("2012-12-25")

date(2012, 12, 25)
// date("2012-12-25")
```

## time()

- parameters:
  - `from`: string / date-time
  - or `hour`, `minute`, `second`: number
    - (optional) `offset`: day-time-duration
- result: time

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

Constructs a data-time value from the given parameters.

- parameters (different options):
  - (`date`: date, `time`: time)
    - combines `date` and `time`
  - (`date`: date-time, `time`: time)
    - takes the date component of `date` and combines with `time`
  - (`date`: date-time, `timezone`: string)
    - combines `date` and `timezone`
    - if `date` has a different timezone than `timezone` then it adjusts the time
  - (`from`: string) 
    - parses `from` into a date-time
    - the string should match the format of a [date-time](../language-guide/feel-data-types.md#date-time) literal 
- result: date-time

```js
date and time(date("2012-12-24"),time("T23:59:00")) 
// date and time("2012-12-24T23:59:00")

date and time(date and time("2012-12-25T11:00:00"),time("T23:59:00"))
// date and time("2012-12-25T23:59:00")

date and time(birthday) 
// date and time("2018-04-29T009:30:00")

date and time(@"2020-07-31T14:27:30@Europe/Berlin", "America/Los_Angeles")
// date and time("2020-07-31T05:27:30@America/Los_Angeles")

date and time(@"2020-07-31T14:27:30", "Z")
// date and time("2020-07-31T05:27:30Z")
```

## duration()

- parameters:
  - `from`: string
- result: day-time-duration or year-month-duration

```js
duration(weekDays)
// duration("P5D")

duration(age)
// duration("P32Y")
```

## years and months duration()

- parameters:
  - `from`: date
  - `to`: date
- result: year-month-duration

```js
years and months duration(date("2011-12-22"), date("2013-08-24"))
// duration("P1Y8M")
```

## number()

- parameters:
  - `from`: string
- result: number

```js
number("1500.5") 
// 1500.5
```

## string()

- parameters:
  - `from`: any
- result: string

```js
string(1.1) 
// "1.1"

string(date("2012-12-25"))
// "2012-12-25"
```

## context()

Constructs a context of the given list of key-value pairs. It is the reverse function to [get entries()](feel-built-in-functions-context.md#get-entries).

Each key-value pair must be a context with two entries: `key` and `value`. The entry with name `key` must have a value of the type `string`.

It might override context entries if the keys are equal. The entries are overridden in the same order as the contexts in the given list.

Returns `null` if one of the entries is not a context or if a context doesn't contain the required entries.

- parameters:
  - `entries`: list of contexts
- result: context

```js
context([{"key":"a", "value":1}, {"key":"b", "value":2}])
// {a:1, b:2}
```
