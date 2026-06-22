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

Or, download the [JAR file](https://github.com/camunda/feel-scala/releases) _(feel-engine-${VERSION}-complete.jar)_ and add it to your classpath.

## Use as a Library

The FEEL engine provides an API to evaluate expressions and unary-tests.

**Java:**

```java
final FeelEngine engine = new FeelEngine.Builder()
    .valueMapper(SpiServiceLoader.loadValueMapper())
    .functionProvider(SpiServiceLoader.loadFunctionProvider())
    .build();

final Map<String, Object> variables = Map.of("x", 21);
final Either<FeelEngine.Failure, Object> result = engine.evalExpression("x + 21", variables);

if (result.isRight()) {
    final Object value = result.right().get();
    System.out.println("result is " + value); // result is 42
} else {
    final FeelEngine.Failure failure = result.left().get();
    throw new RuntimeException(failure.message());
}
```

**Scala:**

```scala
val engine = new FeelEngine

val result: Either[Failure, Any] = engine.evalExpression("x + 21", Map("x" -> 21))

result
  .map(value => println(s"result is: $value")) // result is: 42
  .left.map(failure => println(s"failure: $failure"))
```

## Extend

The FEEL engine can be extended and customized by implementing one of the following SPIs (Service Provider Interface). The implementations are loaded via Java's ServiceLoader mechanism.

### Custom Functions

Implement `org.camunda.feel.context.JavaFunctionProvider` (Java) or `org.camunda.feel.context.CustomFunctionProvider` (Scala) to provide custom functions.

```java
public class CustomFunctionProvider extends JavaFunctionProvider {

    @Override
    public Optional<JavaFunction> resolveFunction(String functionName) {
        if (functionName.equals("incr")) {
            return Optional.of(new JavaFunction(List.of("x"), args -> {
                ValNumber arg = (ValNumber) args.get(0);
                return new ValNumber(arg.value().add(BigDecimal.ONE));
            }));
        }
        return Optional.empty();
    }

    @Override
    public Collection<String> getFunctionNames() {
        return List.of("incr");
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
public class MyClock extends FeelEngineClock {
    @Override
    public ZonedDateTime getCurrentTime() {
        return ...; // return the current time from your clock
    }
}
```

Register the clock by creating the file `META-INF/services/org.camunda.feel.FeelEngineClock` with the fully qualified class name.

## Contribution

Contributions are welcome 🎉 Please have a look at the [Contribution Guide](./CONTRIBUTING.md).

Found a bug? Please [report it](https://github.com/camunda/feel-scala/issues).

## License

[Apache License, Version 2.0](./LICENSE)
