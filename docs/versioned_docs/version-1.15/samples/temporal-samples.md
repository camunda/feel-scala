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

## Calculate the Duration between Times

Return the duration between now and the next time it is 09:00 in Europe/Berlin timezone.

```js
{
  time: time("09:00:00@Europe/Berlin"),
  date: if (time(now()) < time) then today() else today() + duration("P1D"),
  duration: date and time(date, time) - now()
}.duration
```

Output:
``` 
duration("PT18H30M38S")
```

## Calculate the Next Weekday

Return the next day that is not a weekend at 00:00.

```js
(for x in 1..3 
  return date and time(today(),time("00:00:00Z")) 
    + duration("P"+string(x)+"D")
)[not(day of week(item) in ("Saturday","Sunday"))][1]
```

## Change the format of Dates

Transform a given list of date-time values into a custom format.

```js
for d in dates return { 
  date: date(date and time(d)), 
  day: string(date.day),
  month: substring(month of year(date), 1, 3),
  year: string(date.year),
  formatted: day + "-" + month + "-" + year
}.formatted
```

Input:
```js
["2021-04-21T07:25:06.000Z","2021-04-22T07:25:06.000Z"]
```

Output:
```js
["21-Apr-2021","22-Apr-2021"]
```

## Create a Unix Timestamp

Return the current point in time as a Unix timestamp.

```js
(now() - date and time("1970-01-01T00:00Z")) / duration("PT1S") * 1000
```

Output:
```js
1618200039000
```