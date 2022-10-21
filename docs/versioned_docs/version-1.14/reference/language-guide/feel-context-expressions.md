---
id: feel-context-expressions 
title: Context Expressions
---

### Literal

Creates a new context with the given entries. Each entry has a key and a value. The key is either a
name or a string. The value can be any type.
Please reference [FEEL Variables](./feel-variables.md#variable-names) for naming convention for key name.

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

### Get Entry / Path

```js
a.b
```

Accesses the entry with the key `b` of the context `a`. The path is separated by `.` (a dot).

If the value of the entry `b` is also a context then the path can be chained (i.e. `a.b.c`).

```js
{
  a: 2
}.a
// 2

{
  a: {
    b: 3
  }
}.a
// {b: 3}

{
  a: {
    b: 3
  }
}.a.b
// 3
```

### Filter

Filters a list of context elements. It is a special kind of the [filter expression](feel-list-expressions#filter) for lists.  

While filtering, the entries of the current context element can be accessed by their key.

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

Extracts the entries of a list of context elements by a given key (i.e. a projection). It returns a
list that contains the values of the context elements for the given key. 

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
