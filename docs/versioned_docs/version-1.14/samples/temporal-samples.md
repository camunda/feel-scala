---
id: temporal-samples
title: Temporal Samples
---

## Compare a Date with Offset

Check if a date is at least 6 months before another.

```js
date1 < date2 + duration("P6M")
```

## Calculate the Age

Return the current age of a person based on a given birthday.

```js
years and months duration(date(birthday), today()).years
```

## Check for Weekend

Check if the current day is on weekend or not.

```js
day of week(today()) in ("Saturday","Sunday")
```

## Calculate the Duration between Dates

Return the duration between now and the next Tuesday at 08:00.

```js
(for x in 1..7 
  return date and time(today(),time("08:00:00Z")) 
    + duration("P"+string(x)+"D")
)[day of week(item) = "Tuesday"][1] - now()
```

## Calculate the Next Weekday

Return the next day that is not a weekend at 00:00.

```js
(for x in 1..3 
  return date and time(today(),time("00:00:00Z")) 
    + duration("P"+string(x)+"D")
)[not(day of week(item) in ("Saturday","Sunday"))][1]
```