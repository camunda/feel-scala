# FEEL-Scala

[![Maven Central](https://img.shields.io/maven-central/v/org.camunda.feel/feel-engine)](https://mvnrepository.com/artifact/org.camunda.feel/feel-engine)

A parser and interpreter for FEEL (Friendly Enough Expression Language) written in Scala (see [What is FEEL?](https://docs.camunda.io/docs/components/modeler/feel/what-is-feel/)).

The FEEL engine started as a slack time project, grown into a community-driven project, and is now officially maintained by [Camunda](https://camunda.com/). :rocket:

It is integrated in the following projects:
* [Camunda 7](https://docs.camunda.org/manual/user-guide/dmn-engine/feel/) as part of the DMN engine
* [Camunda 8 (Zeebe)](https://docs.camunda.io/docs/components/concepts/expressions/) as the expression language
* [DMN-Scala](https://github.com/camunda/dmn-scala/) as part of the DMN engine

**Features:** :sparkles:

* Full support for unary-tests and expressions
* Including built-in functions
* Extensible by custom functions and object mappers

## Usage

Please have a look at the [Language Guide](https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-expressions-introduction/). It describes how to write FEEL expressions (e.g. data types, language constructs, built-in functions, etc.).

Want to try it out? Use the [FEEL Playground](https://feel-playground.camunda.com/playground) to evaluate FEEL expressions directly in the browser.

## Install

Add the FEEL engine as a dependency to your project:

```xml
<dependency>
  <groupId>org.camunda.feel</groupId>
  <artifactId>feel-engine</artifactId>
  <version>${VERSION}</version>
</dependency>
```

## Use as a Library

The FEEL engine provides an API to evaluate expressions and unary-tests.

**Java:**

```java
final FeelEngineApi engine = FeelEngineBuilder.forJava().build();

final Map<String, Object> variables = Map.of("x", 21);
final EvaluationResult evaluationResult = engine.evaluateExpression("x + 21", variables);

if (evaluationResult.isSuccess()) {
    final Object value = evaluationResult.result();
    System.out.println("result is " + value); // result is 42
} else {
    throw new RuntimeException(evaluationResult.failure().message());
}
```

**Scala:**

```scala
val engine: FeelEngineApi = FeelEngineBuilder().build()

val evaluationResult: EvaluationResult = engine.evaluateExpression("x + 21", Map("x" -> 21))

if (evaluationResult.isSuccess) {
    println(s"result: ${evaluationResult.result}")
} else {
    println(s"failure: ${evaluationResult.failure.message}")
}
```

### Use as a script engine

The FEEL engine implements Java's script engine API [JSR 223](https://www.jcp.org/en/jsr/detail?id=223).

The FEEL expression evaluation is registered under the following names:

* `feel`
* `feel-scala`
* `http://www.omg.org/spec/FEEL/20140401` (FEEL namespace)

The FEEL unary-tests evaluation is registered under the following names:

* `feel-unary-tests`
* `feel-scala-unary-tests`

## Extend

The FEEL engine can be extended and customized by implementing one of the following SPIs (Service Provider Interface). The implementations are loaded via Java's ServiceLoader mechanism.

### Custom Functions

Implement `org.camunda.feel.context.JavaFunctionProvider` (Java) or `org.camunda.feel.context.CustomFunctionProvider` (Scala) to provide custom functions.

```java
public class CustomJavaFunctionProvider extends JavaFunctionProvider {
    private static final Map<String, JavaFunction> functions = new HashMap<>();

    static {
        final JavaFunction function = new JavaFunction(Arrays.asList("x"), args -> {
            final ValNumber arg = (ValNumber) args.get(0);

            int x = arg.value().intValue();

            return new ValNumber(BigDecimal.valueOf(x - 1));
        });

        functions.put("decr", function);
    }

    @Override
    public Optional<JavaFunction> resolveFunction(String functionName) {
        return Optional.ofNullable(functions.get(functionName));
    }

    @Override
    public Collection<String> getFunctionNames() {
        return functions.keySet();
    }
}
```

Register the provider by creating the file `META-INF/services/org.camunda.feel.context.CustomFunctionProvider` with the fully qualified class name.

### Custom Value Mapper

Implement `org.camunda.feel.valuemapper.JavaCustomValueMapper` (Java) or `org.camunda.feel.valuemapper.CustomValueMapper` (Scala) to transform custom types.

```java
public class CustomValueMapper extends JavaCustomValueMapper {

    @Override
    public Optional<Val> toValue(Object x, Function<Object, Val> innerValueMapper) {
        if (x instanceof MyType) {
            return Optional.of(new ValString(((MyType) x).getName()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> unpackValue(Val value, Function<Val, Object> innerValueMapper) {
        return Optional.empty();
    }

    @Override
    public int priority() {
        return 1;
    }
}
```

Register the mapper by creating the file `META-INF/services/org.camunda.feel.valuemapper.CustomValueMapper` with the fully qualified class name.

### Custom Clock

Implement `org.camunda.feel.FeelEngineClock` to replace the system clock used by the engine (e.g. for testing).

```java
import java.time.Instant;
import java.time.ZonedDateTime;

public class MyClock extends FeelEngineClock {
    @Override
    public ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now(); // return the current time from your clock
    }
}
```

Register the clock by creating the file `META-INF/services/org.camunda.feel.FeelEngineClock` with the fully qualified class name.

## Contribution

Contributions are welcome 🎉 Please have a look at the [Contribution Guide](./CONTRIBUTING.md).

## License

[Apache License, Version 2.0](./LICENSE)
