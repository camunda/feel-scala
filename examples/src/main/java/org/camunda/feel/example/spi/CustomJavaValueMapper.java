package org.camunda.feel.example.spi;

import java.util.Optional;
import java.util.function.Function;

import org.camunda.feel.syntaxtree.Val;
import org.camunda.feel.syntaxtree.ValNumber;
import org.camunda.feel.syntaxtree.ValString;
import org.camunda.feel.valuemapper.JavaCustomValueMapper;

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

  interface Custom {

    String getName();
  }
}
