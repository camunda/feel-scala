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

## Merge two Lists

Merge two given lists. Each list contains context values with the same structure. Each context has
an entry "id" that identifies the value.

The result is a list that contains all context values grouped by the identifier.

```js
 {
   ids: union(x.files.id,y.files.id),
   getById: function (files,fileId) 
     if (count(files[id=fileId]) > 0) 
     then files[id=fileId][1] 
     else {},
   merge: for id in ids return put all(getById(x.files, id), getById(y.files, id))
 }.merge
```

Input:

```js
{
  "x": {"files": [
    {"id":1, "content":"a"},
    {"id":2, "content":"b"}
  ]},
  "y": {"files": [
    {"id":1, "content":"a2"},
    {"id":3, "content":"c"}
  ]} 
 }
```

Output: 

```js
[
  {"id":1, "content":"a2"},
  {"id":2, "content":"b"},
  {"id":3, "content":"c"}
]
```
