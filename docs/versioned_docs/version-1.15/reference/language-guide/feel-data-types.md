---
id: feel-data-types
title: Data types
description: "This document outlines data types, including null, number, string, boolean, and more."
---

FEEL defines the following types:

### Null

Nothing, null, or nil (i.e. the value is not present).

- Java Type: `null`

```js
null
```

### Number

A whole or floating point number. The number can be negative.

- not-a-number (NaN), positive/negative infinity are represented as `null`
- Java Type: `java.math.BigDecimal`

```js
1

2.3

.4
    
-5
```

### String

A sequence of characters enclosed in double quotes `"`. The sequence can also contain escaped characters starting with `\` (e.g. `\'`, `\"`, `\\`, `\n`, `\r`, `\t`, unicode like `\u269D` or `\U101EF`).

- Java Type: `java.lang.String`

```js
"valid"
```

### Boolean

A boolean value. It is either true or false.

- Java Type: `java.lang.Boolean`

```js
true

false
```

### Date

A date value without a time component.

- Format: `yyyy-MM-dd`.
- Java Type: `java.time.LocalDate`

```js
date("2017-03-10")

@"2017-03-10"
```

### Time

A local or zoned time. The time can have an offset or time zone id.

- Format: `HH:mm:ss` / `HH:mm:ss+/-HH:mm` / `HH:mm:ss@ZoneId`
- Java Type: `java.time.LocalTime` / `java.time.OffsetTime`

```js
time("11:45:30") 
time("13:30")
time("11:45:30+02:00")
time("10:31:10@Europe/Paris")

@"11:45:30"
@"13:30"
@"11:45:30+02:00"
@"10:31:10@Europe/Paris"
```

### Date-time

A date with a local or zoned time component. The time can have an offset or time zone id.

- Format: `yyyy-MM-dd'T'HH:mm:ss` / `yyyy-MM-dd'T'HH:mm:ss+/-HH:mm` / `yyyy-MM-dd'T'HH:mm:ss@ZoneId`
- Java Type: `java.time.LocalDateTime` / `java.time.DateTime`

```js
date and time("2015-09-18T10:31:10")
date and time("2015-09-18T10:31:10+01:00")
date and time("2015-09-18T10:31:10@Europe/Paris")

@"2015-09-18T10:31:10"
@"2015-09-18T10:31:10+01:00"
@"2015-09-18T10:31:10@Europe/Paris"
```

### Days-time-duration

A duration based on seconds. It can contain days, hours, minutes, and seconds.

- Format: `PxDTxHxMxS`
- Java Type: `java.time.Duration`

```js
duration("P4D")
duration("PT2H")
duration("PT30M")
duration("P1DT6H")

@"P4D"
@"PT2H"
@"PT30M"
@"P1DT6H"
```

### Years-months-duration

A duration based on the calendar. It can contain years and months.

- Format: `PxYxM`
- Java Type: `java.time.Period`

```js
duration("P2Y")
duration("P6M")
duration("P1Y6M")

@"P2Y"
@"P6M"
@"P1Y6M"
```

### List

A list of elements. The elements can be of any type. The list can be empty.

- Java Type: `java.util.List`

```js
[]
[1,2,3]
["a","b"]

[["list"], "of", [["lists"]]]
```

### Context

A list of entries. Each entry has a key and a value. The key is either a name or a string. The value
can be any type. The context can be empty.

- Java Type: `java.util.Map`

```js
{}

{a:1}
{b: 2, c: "valid"}
{nested: {d: 3}}

{"a": 1}
{"b": 2, "c": "valid"}
```
