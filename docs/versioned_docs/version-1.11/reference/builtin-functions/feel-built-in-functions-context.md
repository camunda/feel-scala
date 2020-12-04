---
id: feel-built-in-functions-context
title: Context Functions
---

## get value()

Returns the value of the context entry with the given key.

* parameters:
  * `context`: context
  * `key`: string
* result: any

```js
get value({foo: 123}, "foo") 
// 123
```

## get entries()

Returns the entries of the context as list of key-value-pairs.

* parameters:
  * `context`: context
* result: list of context which contains two entries for "key" and "value"

```js
get entries({foo: 123})
// [{key: "foo", value: 123}]
```