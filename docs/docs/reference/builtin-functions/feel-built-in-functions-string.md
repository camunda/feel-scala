---
id: feel-built-in-functions-string
title: String functions
description: "This document outlines built-in string functions and examples."
---

import MarkerCamundaExtension from "@site/src/components/MarkerCamundaExtension";

## substring(string, start position)

Returns a substring of the given value starting at `start position`. 

**Function signature**

```js
substring(string: string, start position: number): string
```

The `start position` starts at the index `1`. The last position is `-1`.

**Examples**

```js
substring("foobar", 3) 
// "obar"
```

## substring(string, start position, length)

Returns a substring of the given value starting at `start position`.

**Function signature**

```js
substring(string: string, start position: number, length: number): string
```

The `start position` starts at the index `1`. The last position is `-1`.

**Examples**

```js
substring("foobar", 3, 3) 
// "oba"
```

## string length(string)

Returns the number of characters in the given value.

**Function signature**

```js
string length(string: string): number
```

**Examples**

```js
string length("foo") 
// 3
```

## upper case(string)

Returns the given value with all characters are uppercase.

**Function signature**

```js
upper case(string: string): string
```

**Examples**

```js
upper case("aBc4") 
// "ABC4"
```

## lower case(string)

Returns the given value with all characters are lowercase.

**Function signature**

```js
lower case(string: string): string
```

**Examples**

```js
lower case("aBc4") 
// "abc4"
```

## substring before(string, match)

Returns a substring of the given value that contains all characters before `match`.

**Function signature**

```js
substring before(string: string, match: string): string
```

**Examples**

```js
substring before("foobar", "bar") 
// "foo"
```

## substring after(string, match)

Returns a substring of the given value that contains all characters after `match`.

**Function signature**

```js
substring after(string: string, match: string): string
```

**Examples**

```js
substring after("foobar", "ob") 
// "ar"
```

## contains(string, match)

Returns `true` if the given value contains the substring `match`. Otherwise, returns `false`.

**Function signature**

```js
contains(string: string, match: string): boolean
```

**Examples**

```js
contains("foobar", "of") 
// false
```

## starts with(string, match)

Returns `true` if the given value starts with the substring `match`. Otherwise, returns `false`.

**Function signature**

```js
starts with(string: string, match: string): boolean
```

**Examples**

```js
starts with("foobar", "fo") 
// true
```

## ends with(string, match)

Returns `true` if the given value ends with the substring `match`. Otherwise, returns `false`.

**Function signature**

```js
ends with(string: string, match: string): boolean
```

**Examples**

```js
ends with("foobar", "r") 
// true
```

## matches(input, pattern)

Returns `true` if the given value matches the `pattern`. Otherwise, returns `false`.

**Function signature**

```js
matches(input: string, pattern: string): boolean
```

The `pattern` is a string that contains a regular expression. 

**Examples**

```js
matches("foobar", "^fo*bar") 
// true
```

## matches(input, pattern, flags)

Returns `true` if the given value matches the `pattern`. Otherwise, returns `false`.

**Function signature**

```js
matches(input: string, pattern: string, flags: string): boolean
```

The `pattern` is a string that contains a regular expression.

The `flags` can contain one or more of the following characters:
- `s` (dot-all)
- `m` (multi-line)
- `i` (case insensitive)
- `x` (comments)

**Examples**

```js
matches("FooBar", "foo", "i")
// true
```

## replace(input, pattern, replacement)

Returns the resulting string after replacing all occurrences of `pattern` with `replacement`.

**Function signature**

```js
replace(input: string, pattern: string, replacement: string): string
```

The `pattern` is a string that contains a regular expression.

The `replacement` can access the match groups by using `$` and the number of the group, for example, 
`$1` to access the first group.

**Examples**

```js
replace("abcd", "(ab)|(a)", "[1=$1][2=$2]")
// "[1=ab][2=]cd"

replace("0123456789", "(\d{3})(\d{3})(\d{4})", "($1) $2-$3")
// "(012) 345-6789"
```

## replace(input, pattern, replacement, flags)

Returns the resulting string after replacing all occurrences of `pattern` with `replacement`.

**Function signature**

```js
replace(input: string, pattern: string, replacement: string, flags: string): string
```

The `pattern` is a string that contains a regular expression.

The `replacement` can access the match groups by using `$` and the number of the group, for example,
`$1` to access the first group.

The `flags` can contain one or more of the following characters:
- `s` (dot-all)
- `m` (multi-line)
- `i` (case insensitive)
- `x` (comments)

**Examples**

```js
replace("How do you feel?", "Feel", "FEEL", "i")
// "How do you FEEL?"
```

## split(string, delimiter)

Splits the given value into a list of substrings, breaking at each occurrence of the `delimiter` pattern.

**Function signature**

```js
split(string: string, delimiter: string): list<string>
```

The `delimiter` is a string that contains a regular expression.

**Examples**

```js
split("John Doe", "\s" ) 
// ["John", "Doe"]

split("a;b;c;;", ";")
// ["a", "b", "c", "", ""]
```

## extract(string, pattern)

<MarkerCamundaExtension></MarkerCamundaExtension>

Returns all matches of the pattern in the given string. Returns an empty list if the pattern doesn't
match.

**Function signature**

```js
extract(string: string, pattern: string): list<string>
```

The `pattern` is a string that contains a regular expression.

**Examples**

```js
extract("references are 1234, 1256, 1378", "12[0-9]*")
// ["1234","1256"]
```
