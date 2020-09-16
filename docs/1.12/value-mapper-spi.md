---
title: Engine Reference: Value Mapper SPI
---

## Value Mapper SPI

The value mapper is used while evaluating expressions and unary tests to
* transform a variable into a FEEL data type (e.g. when it is referenced in an expression `x + 1`)
* transform the result of the expression or unary tests from a FEEL data type into a common data type (e.g. to String or BigDecimal/Long)

Using the SPI, the transformation can be customized to support more/custom data types, or changing the data type of the result.

### Implement a Value Mapper using Scala

Create a sub-class of `org.camunda.feel.valuemapper.CustomValueMapper`. Implement the method `toVal()` and `unpackVal()` to transform the object. Set the `priority` of the value mapper to define the precedence compared to the other mappers. 

```scala
class MyValueMapper extends CustomValueMapper {

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = x match {
    case c: Custom => Some(ValString(c.getName))
    case _ => None
  }

  override def unpackVal(value: Val, innerValueMapper: Val => Any): Option[Any] = value match {
    case ValNumber(number) => Some(number.doubleValue) // map BigDecimal to Double
    case _ => None
  }
	
  override val priority: Int = 1

}
```

### Implement a Value Mapper using Java

Using Java, create a sub-class of `org.camunda.feel.valuemapper.JavaCustomValueMapper`. It is basically equal to the Scala one but with Java instead of Scala types.

```java
public class CustomJavaValueMapper extends JavaCustomValueMapper {

  @Override
  public Optional<Val> toValue(Object x, Function<Object, Val> innerValueMapper) {
    if (x instanceof Custom) {
      final Custom c = (Custom) x;
      return Optional.of(new ValString(c.getName()));

    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Object> unpackValue(Val value, Function<Val, Object> innerValueMapper) {
    if (value instanceof ValNumber) {
      final ValNumber number = (ValNumber) value;
      return Optional.of(number.value().doubleValue()); // map BigDecimal to Double

    } else {
      return Optional.empty();
    }
  }

  @Override
  public int priority() {
    return 1;
  }
}
```

### Register the Value Mapper

Depending how the FEEL engine is used, the value mapper can be passed directly on creation, or is loaded via Java ServiceLoader mechanism. 

In the second case, create a new file `org.camunda.feel.valuemapper.CustomValueMapper` in the folder `META-INF/services/`. It must contain the full qualified name of the value mapper.

```
org.camunda.feel.example.valuemapper.MyValueMapper
```
