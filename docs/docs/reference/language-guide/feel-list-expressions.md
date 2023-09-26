---
id: feel-list-expressions
title: List expressions
description: "This document outlines list expressions and examples."
---

### Literal

Creates a new list of the given elements. The elements can be of any type.

```js
[1,2,3,4]
```

A list value can embed other list values.

```js
[[1,2], [3,4], [5,6]]
```

### Get element

```js
a[i]
```

Accesses an element of the list `a` at index `i`. The index starts at `1`.

If the index is out of the range of the list, it returns `null`.

```js
[1,2,3,4][1]           
// 1

[1,2,3,4][2]
// 2    

[1,2,3,4][4]                                   
// 4

[1,2,3,4][5]
// null
    
[1,2,3,4][0]                                   
// null
```

If the index is negative, it starts counting the elements from the end of the list. The last
element of the list is at index `-1`.

```js
[1,2,3,4][-1]                                  
// 4

[1,2,3,4][-2]                                  
// 3

[1,2,3,4][-5]                                   
// null
```

:::caution be careful!
The index of a list starts at `1`. In other languages, the index starts at `0`.
:::

### Filter

```js
a[c]
```

Filters the list `a` by the condition `c`. The result of the expression is a list that contains all
elements where the condition `c` evaluates to `true`. The other elements are excluded.

While filtering, the current element is assigned to the variable `item`.

```js
[1,2,3,4][item > 2]   
// [3,4]

[1,2,3,4][item > 10]
// []

[1,2,3,4][even(item)]
// [2,4]
```

### Some

```js
some a in b satisfies c
```

Iterates over the list `b` and evaluate the condition `c` for each element in the list. The current
element is assigned to the variable `a`.

It returns `true` if `c` evaluates to `true` for **one or more** elements of `b`. Otherwise, it
returns `false`.

```js
some x in [1,2,3] satisfies x > 2         
// true

some x in [1,2,3] satisfies x > 5   
// false

some x in [1,2,3] satisfies even(x)
// true

some x in [1,2], y in [2,3] satisfies x < y  
// true
```

### Every

Iterates over the list `b` and evaluate the condition `c` for each element in the list. The current
element is assigned to the variable `a`.

It returns `true` if `c` evaluates to `true` for **all** elements of `b`. Otherwise, it
returns `false`.

```js
every x in [1,2,3] satisfies x >= 1   
// true

every x in [1,2,3] satisfies x >= 2     
// false

every x in [1,2,3] satisfies even(x)
// false

every x in [1,2], y in [2,3] satisfies x < y 
// false
```
