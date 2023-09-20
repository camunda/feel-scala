---
id: changelog 
title: Changelog 
slug: /changelog/
---

import MarkerChangelogVersion from "@site/src/components/MarkerChangelogVersion";

This page contains an overview of the released versions and highlights the major changes from a user
point of view (i.e. focus on features). The complete changelog, including the patch
versions, can be found on the [GitHub release page](https://github.com/camunda/feel-scala/releases).

# 1.17

<MarkerChangelogVersion versionZeebe="8.3.0" versionC7="not yet" />

**Expressions:**

* Overhauled error handling. Instead of failing the evaluation, for example, because of a non-existing
  variable or context entry, it handles these cases and returns `null`. 

**Built-in functions:**

* New built-in
  function [duplicate values()](../reference/builtin-functions/feel-built-in-functions-list#duplicate-valueslist)
  to find duplicate list items
* New built-in
  function [get or else()](../reference/builtin-functions/feel-built-in-functions-boolean#get-or-elsevalue-default)
  to handle `null` values
* New built-in
  function [assert()](../reference/builtin-functions/feel-built-in-functions-boolean#assertvalue-condition)
  to fail the evaluation if a condition is not met

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.17.0).

# 1.16

<MarkerChangelogVersion versionZeebe="8.2.0" versionC7="not yet" />


**Built-in functions:**

* New built-in function [get value()](../reference/builtin-functions/feel-built-in-functions-context#get-valuecontext-keys) to access a context with a dynamic path
* New built-in function [context put()](../reference/builtin-functions/feel-built-in-functions-context#context-putcontext-keys-value) to insert a nested value in a context
* New built-in function [last day of month()](../reference/builtin-functions/feel-built-in-functions-temporal#last-day-of-monthdate) to get the last day of a month
* New built-in function [date and time()](../reference/builtin-functions/feel-built-in-functions-conversion#date-and-timedate-timezone) to get a date-time for a timezone
* New built-in function [random number()](../reference/builtin-functions/feel-built-in-functions-numeric#random-number) to get a random number

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.16.0).

## 1.15

<MarkerChangelogVersion versionZeebe="8.1.0" versionC7="7.19.0" />

**Expressions:**

* New `@` notation for [temporal literals](../reference/language-guide/feel-temporal-expressions.md#literal)

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.15.0).

## 1.14

<MarkerChangelogVersion versionZeebe="1.3.1" versionC7="7.18.0" />

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

<MarkerChangelogVersion versionZeebe="1.0.0" versionC7="7.15.0" />

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

<MarkerChangelogVersion versionZeebe="0.25.0" versionC7="7.14.0" />

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

<MarkerChangelogVersion versionZeebe="0.23.0" versionC7="7.13.0" />

**Expressions:**

* Access the [element of a list](../reference/language-guide/feel-list-expressions.md#get-element) using a numeric variable
* Disable [external functions](../reference/language-guide/feel-functions.md#external) by default for security reasons

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.11.0).
