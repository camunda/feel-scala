---
id: context-samples
title: Context expressions
---

## Validate Data

Validate journal entries and return all violations.

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

## Structure a Calculation

Calculate the minimum age of a given list of birthdays.

```js
{
  age: function(birthday) (today() - birthday).years,
  ages: for birthday in birthdays return age(birthday),
  minAge: min(ages)
}.minAge
```
