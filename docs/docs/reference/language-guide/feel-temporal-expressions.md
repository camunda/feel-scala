---
id: feel-temporal-expressions 
title: Temporal Expressions
---

### Literal

Creates a new temporal value. 

```js
date("2020-04-06")

time("08:00:00")
time("08:00:00+02:00")
time("08:00:00@Europe/Berlin")

date and time("2020-04-06T08:00:00")
date and time("2020-04-06T08:00:00+02:00")
date and time("2020-04-06T08:00:00@Europe/Berlin")

duration("P5D")
duration("PT6H")

duration("P1Y6M")
duration("P3M")
```

### Addition

<table>
  <tr>
    <th>First argument</th>
    <th>Second argument</th>
    <th>Result</th>
  </tr>

  <tr>
    <td>date</td>
    <td>duration</td>
    <td>date</td>
  </tr>

  <tr>
    <td>time</td>
    <td>days-time-duration</td>
    <td>time</td>
  </tr>

  <tr>
    <td>date-time</td>
    <td>duration</td>
    <td>date-time</td>
  </tr>

  <tr>
    <td>duration</td>
    <td>date</td>
    <td>date</td>
  </tr>

  <tr>
    <td>duration</td>
    <td>time</td>
    <td>time</td>
  </tr>

  <tr>
    <td>duration</td>
    <td>date-time</td>
    <td>date-time</td>
  </tr>

  <tr>
    <td>duration</td>
    <td>duration</td>
    <td>duration</td>
  </tr>

</table>


```js
date("2020-04-06") + duration("P1D")
// date("2020-04-07")

time("08:00:00") + duration("PT1H")
// time("09:00:00")

date and time("2020-04-06T08:00:00") + duration("P7D")
// date and time("2020-04-13T08:00:00")

duration("P2D") + duration("P5D")
// duration("P7D")
```  

### Subtraction

<table>
  <tr>
    <th>First argument</th>
    <th>Second argument</th>
    <th>Result</th>
  </tr>

  <tr>
    <td>date</td>
    <td>date</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>date</td>
    <td>duration</td>
    <td>date</td>
  </tr>

  <tr>
    <td>time</td>
    <td>time</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>time</td>
    <td>days-time-duration</td>
    <td>time</td>
  </tr>

  <tr>
    <td>date-time</td>
    <td>date-time</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>date-time</td>
    <td>duration</td>
    <td>date-time</td>
  </tr>

  <tr>
    <td>days-time-duration</td>
    <td>days-time-duration</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>years-months-duration</td>
    <td>years-months-duration</td>
    <td>years-months-duration</td>
  </tr>

</table>

```js
date("2020-04-06") - date("2020-04-01")
// duration("P5D")

date("2020-04-06") - duration("P5D")
// date("2020-04-01")

time("08:00:00") - time("06:00:00")
// duration("PT2H")

time("08:00:00") - duration("PT2H")
// time("06:00:00")

duration("P7D") - duration("P2D")
// duration("P5D")

duration("P1Y") - duration("P3M")
// duration("P9M")
```

### Multiplication

<table>
  <tr>
    <th>First argument</th>
    <th>Second argument</th>
    <th>Result</th>
  </tr>

  <tr>
    <td>days-time-duration</td>
    <td>number</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>number</td>
    <td>days-time-duration</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>years-months-duration</td>
    <td>number</td>
    <td>years-months-duration</td>
  </tr>

  <tr>
    <td>number</td>
    <td>years-months-duration</td>
    <td>years-months-duration</td>
  </tr>

</table>

```js
duration("P1D") * 5
// duration("P5D")

duration("P1M") * 6
// duration("P6M")
```

### Division

<table>
  <tr>
    <th>First argument</th>
    <th>Second argument</th>
    <th>Result</th>
  </tr>

  <tr>
    <td>days-time-duration</td>
    <td>days-time-duration</td>
    <td>number</td>
  </tr>

  <tr>
    <td>days-time-duration</td>
    <td>number</td>
    <td>days-time-duration</td>
  </tr>

  <tr>
    <td>years-months-duration</td>
    <td>years-months-duration</td>
    <td>number</td>
  </tr>

  <tr>
    <td>years-months-duration</td>
    <td>number</td>
    <td>years-months-duration</td>
  </tr>

</table>

```js
duration("P5D") / duration("P1D")  
// 5

duration("P5D") / 5
// duration("P1D")

duration("P1Y") / duration("P1M")
// 12

duration("P1Y") / 12
// duration("P1M")
```

### Properties

A temporal value has multiple properties for its components. The following properties are available
for the given types:

<table>
  <tr>
    <th>Property</th>
    <th>Available for</th>
    <th>Description</th>
</tr>

  <tr>
    <td>year</td>
    <td>date, date-time</td>
    <td>the year as number</td>
  </tr>

  <tr>
    <td>month</td>
    <td>date, date-time</td>
    <td>the month as number [1..12], where 1 is January</td>
  </tr>

  <tr>
    <td>day</td>
    <td>date, date-time</td>
    <td>the day of the month as number [1..31]</td>
  </tr>

  <tr>
    <td>weekday</td>
    <td>date, date-time</td>
    <td>the day of the week as number [1..7], where 1 is Monday</td>
  </tr>

  <tr>
    <td>hour</td>
    <td>time, date-time</td>
    <td>the hour of the day as number [0..23]</td>
  </tr>

  <tr>
    <td>minute</td>
    <td>time, date-time</td>
    <td>the minute of the hour as number [0..59]</td>
  </tr>

  <tr>
    <td>second</td>
    <td>time, date-time</td>
    <td>the second of the minute as number [0..59]</td>
  </tr>

  <tr>
    <td>time offset</td>
    <td>time, date-time</td>
    <td>the duration offset corresponding to the timezone or null</td>
  </tr>

  <tr>
    <td>timezone</td>
    <td>time, date-time</td>
    <td>the timezone identifier or null</td>
  </tr>

  <tr>
    <td>days</td>
    <td>days-time-duration</td>
    <td>the normalized days component as number</td>
  </tr>

  <tr>
    <td>hours</td>
    <td>days-time-duration</td>
    <td>the normalized hours component as number [0..23]</td>
  </tr>

  <tr>
    <td>minutes</td>
    <td>days-time-duration</td>
    <td>the normalized minutes component as number [0..59]</td>
  </tr>

  <tr>
    <td>seconds</td>
    <td>days-time-duration</td>
    <td>the normalized seconds component as number [0..59]</td>
  </tr>

  <tr>
    <td>years</td>
    <td>years-months-duration</td>
    <td>the normalized years component as number</td>
  </tr>

  <tr>
    <td>months</td>
    <td>years-months-duration</td>
    <td>the normalized months component as number [0..11]</td>
  </tr>

</table>

```js
date("2020-04-06").year
// 2020

date("2020-04-06").month
// 4

date("2020-04-06").weekday
// 1

time("08:00:00").hour
// 8 

date and time("2020-04-06T08:00:00+02:00").time offset
// duration("PT2H") 

date and time("2020-04-06T08:00:00@Europe/Berlin").timezone
// "Europe/Berlin"

duration("PT2H30M").hours
// 2

duration("PT2H30M").minutes
// 30

duration("P6M").months
// 6
```