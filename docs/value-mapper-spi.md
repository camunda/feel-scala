---
title: Engine Reference: Value Mapper SPI
---

## Value Mapper SPI

The value mapper is used while evaluating expressions and unary tests to
* transform a variable into a FEEL data type (e.g. when it is referenced in an expression `x + 1`)
* transform the result of the expression or unary tests from a FEEL data type into a common data type (e.g. to String or BigDecimal/Long)

Using the SPI, the transformation can be customized to support more/custom data types, or changing the data type of the result.

### Implement a Value Mapper using Scala

Create a sub-class of `org.camunda.feel.spi.CustomValueMapper`. Override the method `toVal()` and/or `unpackVal()` to customize the default behavior.

```scala
class MyValueMapper extends CustomValueMapper {

  override def toVal(x: Any): Val = x match {
    case c: Custom => ValString(c.getName)
    case _ => super.toVal(x)
  }

  override def unpackVal(value: Val): Any = value match {
    case ValNumber(number) => number.doubleValue // map BigDecimal to Double
    case _ => super.unpackVal(value)
  }
	
}
```

### Implement a Value Mapper using Java

Using Java, create a sub-class of `org.camunda.feel.interpreter.DefaultValueMapper` which implements `org.camunda.feel.spi.CustomValueMapper`. It is equal to the Scala one but need to extend the default implementation explicitly.

```java
public class MyValueMapper extends DefaultValueMapper implements CustomValueMapper  {

    @Override
    public Val toVal(Object x) {

        if (x instanceof Custom) {
            final Custom c = (Custom) x;
            return new ValString(c.getName());

        } else {
            return super.toVal(x);
        }
    }

    @Override
    public Object unpackVal(Val value) {

        if (value instanceof ValNumber) {
            final ValNumber number = (ValNumber) value;
            return number.value().doubleValue(); // map BigDecimal to Double

        } else {
            return super.unpackVal(value);
        }
    }
}
```

### Register the Value Mapper

Depending how the FEEL engine is used, the value mapper can be passed directly on creation, or is loaded via Java ServiceLoader mechanism. 

In the second case, create a new file `org.camunda.feel.spi.CustomValueMapper` in the folder `META-INF/services/`. It must contain the full qualified name of the value mapper.

```
org.camunda.feel.example.spi.MyValueMapper
```
