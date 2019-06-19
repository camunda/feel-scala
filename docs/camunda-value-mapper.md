---
title: Integration into Camunda BPM: Value Mapper
---

## Camunda Value Mapper

If the FEEL engine is used in the context of Camunda BPM then it used a special [value mapper](value-mapper-spi) to transform common Camunda types into FEEL data type and the other way around.

### Transform Variables into FEEL data types

Common Java types:

* `java.math.BigDecimal` -> number
* `java.math.BigInteger` -> number
* `java.util.Date` -> date-time
* `java.time.OffsetDateTime` -> date-time
* `java.time.OffsetTime` -> time
* `java.util.List` -> list
* `java.util.Map` -> context
* `java.lang.Enum` -> string

Joda-Time types:

* `org.joda.time.LocalDate` -> date
* `org.joda.time.LocalTime` -> time
* `org.joda.time.LocalDateTime` -> date-time
* `org.joda.time.Duration` -> day-time-duration
* `org.joda.time.Period` -> year-month-duration

If the variable type is unknown then it is transformed into a context including fields and methods. 

### Transform the Result of a FEEL expression

* null -> `null`
* boolean -> `java.lang.Boolean`
* string -> `java.lang.String`
* number -> `java.lang.Long` / `java.lang.Double` (depending if it's a whole number)
* date -> `java.time.LocalDate`
* time -> `java.time.LocalTime` / `java.time.OffsetTime`
* date-time -> `java.time.LocalDateTime` / `java.time.ZonedDateTime`
* day-time-duration -> `java.time.Duration`
* year-month-duration -> `java.time.Period`
* list -> `java.util.List<Object>`
* context -> `java.util.Map<String, Object>`
