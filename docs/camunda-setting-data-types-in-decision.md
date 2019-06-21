---
title: Integration into Camunda BPM: Setting FEEL Data Types in Decision
---

## Setting FEEL Data Types in Decision

If the FEEL engine is used in the context of Camunda BPM then it adds additional [data type transformers](https://docs.camunda.org/manual/latest/user-guide/dmn-engine/embed/#customizing-and-extending-the-dmn-engine) to the DMN engine. 

By default, the Camunda DMN engine doesn't support all FEEL data types. If you set a type of an expression (e.g. the type of an input expression in a decision table) which is not supported then it ignores the type (i.e. it doesn't fail if the type doesn't match).

### Additional Data Types

* `feel-date` 
* `feel-time`
  * time with offset/timezone
* `feel-local-time` 
  * time without offset/timezone
* `feel-date-time`
  * date-time with offset/timezone
* `feel-local-date-time`
  * date-time without offset/timezone

### Example

In a decision table, the type of an input expression `date("2017-03-10")` can be set to `feel-date`.
 
