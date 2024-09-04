---
id: changelog 
title: Changelog 
slug: /changelog/
---

import MarkerChangelogVersion from "@site/src/components/MarkerChangelogVersion";

This page contains an overview of the released versions and highlights the major changes from a user
point of view (i.e. focus on features). The complete changelog, including the patch
versions, can be found on the [GitHub release page](https://github.com/camunda/feel-scala/releases).

## 1.18

<MarkerChangelogVersion versionZeebe="8.6.0" versionC7="not yet" />

**Built-in functions:**

* New built-in
  function `is empty()`
  to check if a list is empty
* New built-in
  function `trim()`
  to remove leading and trailing spaces of a string
* New built-in
  function `uuid()`
  to create a UUID (Universally Unique Identifier)
* New built-in
  function `to base64()`
  to encode a string in Base64 format

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.18.0).

## 1.17

<MarkerChangelogVersion versionZeebe="8.3.0" versionC7="7.21.0" />

**Expressions:**

* Overhauled error handling. Instead of failing the evaluation, for example, because of a non-existing
  variable or context entry, it handles these cases and returns `null`. 

**Built-in functions:**

* New built-in
  function [duplicate values()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-list#duplicate-valueslist)
  to find duplicate list items
* New built-in
  function [get or else()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-boolean#get-or-elsevalue-default)
  to handle `null` values
* New built-in
  function [assert()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-boolean#assertvalue-condition)
  to fail the evaluation if a condition is not met

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.17.0).

## 1.16

<MarkerChangelogVersion versionZeebe="8.2.0" versionC7="7.20.0" />


**Built-in functions:**

* New built-in function [get value()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#get-valuecontext-keys) to access a context with a dynamic path
* New built-in function [context put()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-putcontext-keys-value) to insert a nested value in a context
* New built-in function [last day of month()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#last-day-of-monthdate) to get the last day of a month
* New built-in function [date and time()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-conversion#date-and-timedate-timezone) to get a date-time for a timezone
* New built-in function [random number()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#random-number) to get a random number

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.16.0).

## 1.15

<MarkerChangelogVersion versionZeebe="8.1.0" versionC7="7.19.0" />

**Expressions:**

* New `@` notation for [temporal literals](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions#literal)

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.15.0).

## 1.14

<MarkerChangelogVersion versionZeebe="1.3.1" versionC7="7.18.0" />

**Built-in functions:**

* New function [extract()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-string#extractstring-pattern)
  that applies a regular expression to a given a string
* New
  function [string join()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-list#string-joinlist)
  that merges a list of strings into a single string
* New [range functions](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-range) to compare
  ranges and scalar values
* New functions to round numeric values:
  * [round up()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-upn-scale)
  * [round down()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-downn-scale)
  * [round half up()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-half-upn-scale)
  * [round half down()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-numeric#round-half-downn-scale)
* Extend function [abs()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#absn) for
  duration values

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.14.0).

## 1.13

<MarkerChangelogVersion versionZeebe="1.0.0" versionC7="7.15.0" />

**Expressions:**

* Access the property [weekday](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions#properties)
  of date and date-time values
* Allow escape sequences in [string literals](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-data-types#string)

**Built-in functions:**

* New
  function [context()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-conversion#contextentries)
  that creates a context from a given key-value list
* New function [put()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-putcontext-key-value) that
  extends a context by a given entry
* New
  function [put all()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-context#context-mergecontexts)
  that merges the given contexts

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.13.0).

## 1.12

<MarkerChangelogVersion versionZeebe="0.25.0" versionC7="7.14.0" />

**Built-in functions:**

* New function [now()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#now) that
  returns the current date-time
* New function [today()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#today)
  that returns the current date
* New
  function [week of year()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#week-of-yeardate)
  that returns the number of the week within the year
* New
  function [month of year()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#month-of-yeardate)
  that returns the name of the month
* New
  function [day of week()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#day-of-weekdate)
  that returns name of the weekday
* New
  function [day of year()](https://docs.camunda.io/docs/components/modeler/feel/builtin-functions/feel-built-in-functions-temporal#day-of-yeardate)
  that returns the number of the day within the year

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.12.0).


## 1.11

<MarkerChangelogVersion versionZeebe="0.23.0" versionC7="7.13.0" />

**Expressions:**

* Access the [element of a list](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-list-expressions#get-element) using a numeric variable
* Disable external functions by default for security reasons

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.11.0).
