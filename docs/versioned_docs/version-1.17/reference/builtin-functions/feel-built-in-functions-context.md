---
id: feel-built-in-functions-context
title: Context functions
description: "This document outlines context functions and a few examples."
---

import MarkerCamundaExtension from "@site/src/components/MarkerCamundaExtension";

## get value(context, key)

Returns the value of the context entry with the given key.

**Function signature**

```js
get value(context: context, key: string): Any
```

**Examples**

```js
get value({foo: 123}, "foo") 
// 123

get value({a: 1}, "b")
// null
```

## get value(context, keys)

<MarkerCamundaExtension></MarkerCamundaExtension>

Returns the value of the context entry for a context path defined by the given keys.

If `keys` contains the keys `[k1, k2]` then it returns the value at the nested entry `k1.k2` of the context.

If `keys` are empty or the nested entry defined by the keys doesn't exist in the context, it returns `null`.

**Function signature**

```js
get value(context: context, keys: list<string>): Any
```

**Examples**

```js
get value({x:1, y: {z:0}}, ["y", "z"]) 
// 0

get value({x: {y: {z:0}}}, ["x", "y"])
// {z:0}

get value({a: {b: 3}}, ["b"])
// null
```

## get entries(context)

Returns the entries of the context as a list of key-value-pairs.

**Function signature**

```js
get entries(context: context): list<context>
```

The return value is a list of contexts. Each context contains two entries for "key" and "value".

**Examples**

```js
get entries({foo: 123})
// [{key: "foo", value: 123}]
```

## context put(context, key, value)

Adds a new entry with the given key and value to the context. Returns a new context that includes the entry.

If an entry for the same key already exists in the context, it overrides the value.

**Function signature**

```js
context put(context: context, key: string, value: Any): context
```

**Examples**

```js
context put({x:1}, "y", 2)
// {x:1, y:2}
```

:::info
The function `context put()` replaced the previous function `put()` (Camunda Extension). The
previous function is deprecated and should not be used anymore.
:::

## context put(context, keys, value)

Adds a new entry with the given value to the context. The path of the entry is defined by the keys. Returns a new context that includes the entry. 

If `keys` contains the keys `[k1, k2]` then it adds the nested entry `k1.k2 = value` to the context.

If an entry for the same keys already exists in the context, it overrides the value.

If `keys` are empty, it returns `null`.

**Function signature**

```js
context put(context: context, keys: list<string>, value: Any): context
```

**Examples**

```js
context put({x:1}, ["y"], 2)
// {x:1, y:2}

context put({x:1, y: {z:0}}, ["y", "z"], 2)
// {x:1, y: {z:2}}

context put({x:1}, ["y", "z"], 2)
// {x:1, y: {z:2}}
```

## context merge(contexts)

Union the given contexts. Returns a new context that includes all entries of the given contexts. 

If an entry for the same key already exists in a context, it overrides the value. The entries are overridden in the same order as in the list of contexts.

**Function signature**

```js
context merge(contexts: list<context>): context
```

**Examples**

```js
context merge([{x:1}, {y:2}])
// {x:1, y:2}

context merge([{x:1, y: 0}, {y:2}])
// {x:1, y:2}
```

:::info
The function `context merge()` replaced the previous function `put all()` (Camunda Extension). The
previous function is deprecated and should not be used anymore.
:::
