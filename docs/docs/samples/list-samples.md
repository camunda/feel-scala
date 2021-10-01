---
id: list-samples
title: List Samples
---

## Filter a List and Return the First Element

Return the first packaging element which unit is "Palette".

```js
(data.attribute.packaging[unit = "Palette"])[1]
```

## Group a List

Group the given list of invoices by their person. 

Each invoice has a person. The persons are extracted from the invoices and are used as a filter for the list.

```js
for p in distinct values(invoices.person) return invoices[person = p]
```

Input:
```js
{"invoices":[
  {"id":1, "person":"A", "amount": 10},
  {"id":2, "person":"A", "amount": 20},
  {"id":3, "person":"A", "amount": 30},
  {"id":4, "person":"A", "amount": 40},
  {"id":5, "person":"B", "amount": 15},
  {"id":6, "person":"B", "amount": 25}
]}
```

Output:
```js
[
  [
    {"id":1, "person":"A", "amount": 10},
    {"id":2, "person":"A", "amount": 20},
    {"id":3, "person":"A", "amount": 30},
    {"id":4, "person":"A", "amount": 40}
  ],
  [
    {"id":5, "person":"B", "amount": 15},
    {"id":6, "person":"B", "amount": 25}
  ]
]
```
