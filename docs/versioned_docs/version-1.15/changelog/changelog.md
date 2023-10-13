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

<SinceVersion versionCloud="8.1.0" versionPlatform="7.19.0" />

**Expressions:**

* New `@` notation for [temporal literals](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions#literal)

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.15.0).

## 1.14

<SinceVersion versionCloud="1.3.1" versionPlatform="7.18.0" />

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

<SinceVersion versionCloud="1.0.0" versionPlatform="7.15.0" />

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

<SinceVersion versionCloud="0.25.0" versionPlatform="7.14.0" />

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

<SinceVersion versionCloud="0.23.0" versionPlatform="7.13.0" />

**Expressions:**

* Access the [element of a list](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-list-expressions#get-element) using a numeric variable
* Disable external functions by default for security reasons

See the full changelog [here](https://github.com/camunda/feel-scala/releases/tag/1.11.0).
