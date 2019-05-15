---
title: FEEL Language Reference: Data Types
---

## FEEL Data Types


### Null

Nothing, null or nil (i.e. the value is not present).

Some operations/functions return `null` if an argument in not valid or types doesn't match.

* Java Type: `null`

```js
null
```

### Number

A whole or floating point number.

* Java Type: `java.math.BigDecimal`

```js
1
2.3
.4
```

### String

* Java Type: `java.lang.String`

```js
"valid"
```

### Boolean

* Java Type: `java.lang.Boolean`

```js
true
false
```

### Date 

* Format: `yyyy-MM-dd`.
* Java Type: `java.time.LocalDate`

```js
date("2017-03-10")
```

### Time 

A local or zoned time. The time can have an offset or time zone id.

* Format: `HH:mm:ss` / `HH:mm:ss+/-HH:mm` / `HH:mm:ss@ZoneId`
* Java Type: `java.time.LocalTime` / `java.time.OffsetTime`

```js
time("11:45:30") 
time("13:30")

time("11:45:30+02:00")

time("10:31:10@Europe/Paris")
```

### Date-Time 

A date with a local or zoned time component. The time can have an offset or time zone id.

* Format: `yyyy-MM-dd'T'HH:mm:ss` / `yyyy-MM-dd'T'HH:mm:ss+/-HH:mm` / `yyyy-MM-dd'T'HH:mm:ss@ZoneId`
* Java Type: `java.time.LocalDateTime` / `java.time.DateTime`

```js
date and time("2015-09-18T10:31:10")

date and time("2015-09-18T10:31:10+01:00")

date and time("2015-09-18T10:31:10@Europe/Paris")
```

### Day-Time-Duration

A duration based on seconds. It can contain days, hours, minutes and seconds.

* Format: `PxDTxHxMxS`
* Java Type: `java.time.Duration`

```js
duration("P4D")
duration("PT2H")
duration("PT30M")
duration("P1DT6H")
```

### Year-Month-Duration

A duration based on the calendar. It can contain years and months.

* Format: `PxYxM`
* Java Type: `java.time.Period`

```js
duration("P2Y")
duration("P6M")
duration("P1Y6M")
```

### List

A list of elements. Can be empty.

* Java Type: `java.util.List`

```js
[]
[1,2,3]
["a","b"]

[["list"], "of", [["lists"]]]
```

### Context

A list of key-value-pairs. Can be empty.

* Java Type: `java.util.Map`

```js
{}
{"a": 1}
{"b": 2, "c": "valid"}

{"nested": {"d": 3}}
```
