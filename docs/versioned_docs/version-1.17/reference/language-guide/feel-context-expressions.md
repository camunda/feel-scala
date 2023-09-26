---
id: feel-context-expressions
title: Context expressions
description: "This document outlines context expressions and examples."
---

### Literal

Creates a new context with the given entries. Each entry has a key and a value. The key is either a
name or a string. The value can be any type.

See the [naming conventions](./feel-variables.md#variable-names) for valid key names.

```js
{
  a: 1,
  b: 2
}
// {a:1, b:2}

{
  "a": 1,
  "b": 2
}
// {a:1, b:2}
```

Inside the context, the previous entries can be accessed.

```js
{
  a: 2,
  b: a * 2
}
// {a:2, b:4}
```

A context value can embed other context values.

```js
{
  a: 1,
  b: {
    c: 2
  }
}
// {a:1, b:{c:2}}
```

### Get entry/path

```js
a.b
```

Accesses the entry with the key `b` of the context `a`. The path is separated by a dot `.`.

If the value of the entry `b` is also a context, the path can be chained (i.e. `a.b.c`).

```js
{a: 2}.a
// 2

{a: {b: 3}}.a
// {b: 3}

{a: {b: 3}}.a.b
// 3
```

If the context `a` doesn't contain an entry with the key `b`, the expression returns `null`.

```js
{a: 1}.b
// null

{a: 1}.b.c
// null
```

### Filter

```js
a[c]
```

Filters the list of context elements `a` by the condition `c`. The result of the expression is a
list that contains all elements where the condition `c` evaluates to `true`. The other elements are
excluded.

While filtering, the current element is assigned to the variable `item` and its entries can be
accessed by their key.

```js
[ 
  {
    a: "p1", 
    b: 5
  },  
  {
    a: "p2", 
    b: 10
  } 
][b > 7] 
// {a: "p2", b: 10}
```

### Projection

```js
a.b
```

Extracts the entries with the key `b` of the list of context elements `a` (i.e. a projection). It
returns a list containing the values of the context elements with the key `b`.

```js
[
  {
    a: "p1",
    b: 5
  },
  {
    a: "p2",
    b: 10
  }
].a     
// ["p1", "p2"]
```

If an element of the list `a` doesn't contain an entry with the key `b`, the result contains `null`
of this element.

```js
[
  {
    a: "p1",
    b: 5
  },
  {
    a: "p2",
    c: 20
  }
].b     
// [5, null]
```


