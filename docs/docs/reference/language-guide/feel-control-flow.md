---
id: feel-control-flow 
title: Control Flow
---

### If Conditions

```js
if c then a else b
```

Executes the expression `a` if the condition `c` evaluates to `true`. Otherwise, it executes the
expression `b`.

```js
if 5 < 10  then "low" else "high"
// "low"

if 12 < 10 then "low" else "high"
// "high"
```

:::info good to know 
If the condition `c` doesn't evaluate to a boolean value (e.g. `null`) then it
executes the expression `b`.

```js
if null then "low" else "high"
// "high"
```

:::

### For Loops

```js
for a in b return c
```

Iterates over the list `b` and executes the expression `c` for each element in the list. The current
element is assigned to the variable `a`. The result of the expression is a list.

If multiple lists are passed to the `for` loop then it iterates over the cross-product of the
elements in the given lists.

```js
for x in [1,2,3] return x * 2
// [2,4,6]

for x in [1,2], y in [3,4] return x * y
// [3, 4, 6, 8]
```

While iterating over the list, the previous elements are assigned to the variable `partial`.

```js
for i in 1..10 return if (i <= 2) then 1 else partial[-1] + partial[-2]
// [1, 1, 2, 3, 5, 8, 13, 21, 34, 55]
```

Instead of a list, the `for` loop can also iterate over a given range. 

```js
for x in 0..8 return 2 ** x
// [1, 2, 4, 8, 16, 32, 64, 128, 256]

for x in 3..1 return 2 * x
// [6,4,2]
```
