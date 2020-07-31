---
title: Engine Reference: FEEL Engine Clock SPI
---

## FEEL Engine Clock SPI

The clock is used when accessing the current time while evaluating expressions and unary tests (e.g. the builtin function `now()`). By default, it uses the system clock. 

Using the SPI, the clock can be replaced by a custom one. For example, if the application uses its own and not the system clock.

### Implement a FEEL Engine Clock 

Create a sub-class of `org.camunda.feel.FeelEngineClock`. Implement the method `getCurrentTime()` to return the current time. 

```scala
class MyClock extends FeelEngineClock {

  override def getCurrentTime(): ZonedDateTime = {
    val currentMillis = ...
    Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault())
  }

}
```


### Register the FEEL Engine Clock

Depending on how the FEEL engine is used, the clock can be passed directly on creation, or is loaded via Java ServiceLoader mechanism. 

In the second case, create a new file `org.camunda.feel.FeelEngineClock` in the folder `META-INF/services/`. It must contain the full qualified name of the class.

```
org.camunda.feel.example.MyClock
```
