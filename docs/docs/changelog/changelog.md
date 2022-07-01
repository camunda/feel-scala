---
id: changelog 
title: Changelog 
slug: /changelog/
---

export const SinceVersion = ({versionCloud, versionPlatform}) => (
<p>
<span style={{backgroundColor: '#25c2a0',borderRadius: '7px',color: '#fff',padding: '0.2rem',marginRight: '0.5rem'}}>Camunda Cloud: {versionCloud}</span>
<span style={{backgroundColor: '#1877F2',borderRadius: '7px',color: '#fff',padding: '0.2rem',}}>Camunda Platform: {versionPlatform}</span>
</p>
);

This page contains an overview of the released versions and highlights the major changes from a user
point of view (i.e. focus on features). The complete changelog, including the patch
versions, can be found on the [GitHub release page](https://github.com/camunda/feel-scala/releases).

## 1.15

<SinceVersion versionCloud="8.1.0" versionPlatform="not yet" />

**Expressions:**

* New `@` notation for [temporal literals](../reference/language-guide/feel-temporal-expressions.md#literal)

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.15.0).

## 1.14

<SinceVersion versionCloud="1.3.1" versionPlatform="7.18.0" />

**Built-in functions:**

* New function [extract()](../reference/builtin-functions/feel-built-in-functions-string.md#extract)
  that applies a regular expression to a given a string
* New
  function [string join()](../reference/builtin-functions/feel-built-in-functions-list.md#string-join)
  that merges a list of strings into a single string
* New [range functions](../reference/builtin-functions/feel-built-in-functions-range.md) to compare
  ranges and scalar values
* New functions to round numeric values:
  * [round up()](../reference/builtin-functions/feel-built-in-functions-numeric.md#round-up)
  * [round down()](../reference/builtin-functions/feel-built-in-functions-numeric.md#round-down)
  * [round half up()](../reference/builtin-functions/feel-built-in-functions-numeric.md#round-half-up)
  * [round half down()](../reference/builtin-functions/feel-built-in-functions-numeric.md#round-half-down)
* Extend function [abs()](../reference/builtin-functions/feel-built-in-functions-temporal.md#abs) for
  duration values

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.14.0).

## 1.13

<SinceVersion versionCloud="1.0.0" versionPlatform="7.15.0" />

**Expressions:**

* Access the property [weekday](../reference/language-guide/feel-temporal-expressions.md#properties)
  of date and date-time values
* Allow escape sequences in [string literals](../reference/language-guide/feel-data-types.md#string)

**Built-in functions:**

* New
  function [context()](../reference/builtin-functions/feel-built-in-functions-conversion.md#context)
  that creates a context from a given key-value list
* New function [put()](../reference/builtin-functions/feel-built-in-functions-context.md#put) that
  extends a context by a given entry
* New
  function [put all()](../reference/builtin-functions/feel-built-in-functions-context.md#put-all)
  that merges the given contexts

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.13.0).

## 1.12

<SinceVersion versionCloud="0.25.0" versionPlatform="7.14.0" />

**Built-in functions:**

* New function [now()](../reference/builtin-functions/feel-built-in-functions-temporal.md#now) that
  returns the current date-time
* New function [today()](../reference/builtin-functions/feel-built-in-functions-temporal.md#today)
  that returns the current date
* New
  function [week of year()](../reference/builtin-functions/feel-built-in-functions-temporal.md#week-of-year)
  that returns the number of the week within the year
* New
  function [month of year()](../reference/builtin-functions/feel-built-in-functions-temporal.md#month-of-year)
  that returns the name of the month
* New
  function [day of week()](../reference/builtin-functions/feel-built-in-functions-temporal.md#day-of-week)
  that returns name of the weekday
* New
  function [day of year()](../reference/builtin-functions/feel-built-in-functions-temporal.md#day-of-year)
  that returns the number of the day within the year

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.12.0).


## 1.11

<SinceVersion versionCloud="0.23.0" versionPlatform="7.13.0" />

**Expressions:**

* Access the [element of a list](../reference/language-guide/feel-list-expressions.md#get-element) using a numeric variable
* Disable [external functions](../reference/language-guide/feel-functions.md#external) by default for security reasons

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.11.0).
