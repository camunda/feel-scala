---
id: feel-built-in-functions-context
title: Context Functions
---

## get value()

Returns the value of the context entry with the given key.

* parameters:
  * `m`: context
  * `key`: string
* result: any

```js
get value(m:{foo: 123}, key:"foo") 
// 123
```

## get entries()

Returns the entries of the context as list of key-value-pairs.

* parameters:
  * `m`: context
* result: list of context which contains two entries for "key" and "value"

```js
get entries(m:{foo: 123})
// [{key: "foo", value: 123}]
```

## put()

Add the given key and value to a context. Returns a new context that includes the entry. It might override an existing entry of the context.  

Returns `null` if the value is not defined.

* parameters:
  * `context`: context
  * `key`: string
  * `value`: any
* result: context  

```js
put({x:1}, "y", 2)
// {x:1, y:2}
```

## put all()

Union the given contexts (two or more). Returns a new context that includes all entries of the given contexts. It might override context entries if the keys are equal. The entries are overridden in the same order as the contexts are passed in the method.    

Returns `null` if one of the values is not a context.

* parameters:
  * `contexts`: contexts as varargs
* result: context  

```js
put all({x:1}, {y:2})
// {x:1, y:2}
```
