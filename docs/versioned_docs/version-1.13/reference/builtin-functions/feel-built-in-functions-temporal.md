---
id: feel-built-in-functions-temporal
title: Temporal Functions
---

## now()

Returns the current date and time including the timezone.

* parameters: no
* result: date-time with timezone

```js
now()
// date and time("2020-07-31T14:27:30@Europe/Berlin")
```

## today()

Returns the current date.

* parameters: no
* result: date

```js
today()
// date("2020-07-31")
```

## day of week()

Returns the day of the week according to the Gregorian calendar. Note that it returns always the english name of the day.

* parameters: 
  * `date`: date/date-time
* result: string

```js
day of week(date("2019-09-17"))
// "Tuesday"
```

## day of year()

Returns the Gregorian number of the day within the year.

* parameters: 
  * `date`: date/date-time
* result: number

```js
day of year(date("2019-09-17"))
// 260
```

## week of year()

Returns the Gregorian number of the week within the year, according to ISO 8601.

* parameters: 
  * `date`: date/date-time
* result: number

```js
week of year(date("2019-09-17"))
// 38
```

## month of year()

Returns the month of the week according to the Gregorian calendar. Note that it returns always the english name of the month.

* parameters: 
  * `date`: date/date-time
* result: string

```js
month of year(date("2019-09-17"))
// "September"
```
