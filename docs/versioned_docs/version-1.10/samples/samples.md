---
id: samples
title: Samples
slug: /samples/
---

Some example FEEL expressions which are used with the engine. Feel free to add your examples ;)

### Date/Time Calculation

Example: check if a date is at least 6 months before another.

```js
date1 < date2 + duration("P6M")
```

### Filter a List and Return the first Element

Example: return the first packaging element which unit is "Palette".

```js
(data.attribute.packaging[unit = "Palette"])[1]
```

### Validate Data using a Context

Example: validate journal entries and return all violations.

```js
{
  check1: {
    error: "Document Type invalid for current year posting",
    violations: collection[documentType = "S2" and glDate > startFiscalYear] 
  },
  check2: {
    error: "Document Type invalid for current year posting",
    violations: collection[ledgerType = "GP" and foreignAmount != null] 
  },
  result: ([check1, check2])[count(violations) > 0] 
}
```

### Structure Calculation using a Context

Example: calculate the minimum age of a given list of birthdays.

```js
{
  age: function(birthday) (today - birthday).years,
  ages: for birthday in birthdays return age(birthday),
  minAge: min(ages)
}.minAge
```
