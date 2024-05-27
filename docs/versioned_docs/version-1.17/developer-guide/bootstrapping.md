---
id: bootstrapping
title: Bootstrapping
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

The FEEL engine can be integrated in two different ways
* as a library by calling the engine API
* as a script engine by using the Java's script engine API

:::tip

Have a look at the [FEEL Playground](/playground/playground.mdx) for trying out FEEL expressions quickly in
development.

:::

### The dependency

Add the engine as dependency to your project's `pom.xml`:

```xml
<dependency>
  <groupId>org.camunda.feel</groupId>
  <artifactId>feel-engine</artifactId>
  <version>${VERSION}</version>
</dependency>
```

Or, download the [JAR file](https://github.com/camunda/feel-scala/releases) _(feel-engine-${VERSION}-complete.jar)_ and copy it into your application.

### Use as a library

The FEEL engine provides APIs to parse and evaluate expressions and unary-tests.

<Tabs
defaultValue="scala"
values={[
{label: 'Scala', value: 'scala'},
{label: 'Java', value: 'java'},
]}>

<TabItem value="scala">

Create a new instance of the class `FeelEngine`. 

```scala
object MyProgram {
  
  val engine = new FeelEngine
  
  def feel(expression: String, context: Map[String, Any]) {
    
    val result: Either[Failure, Boolean] = engine.evalUnaryTests(expression, context)
    // or    
    val result: Either[Failure, Any] = engine.evalExpression(expression, context)
  
    // handle result
    result
        .right.map(value => println(s"result is: $value"))
        .left.map(failure => println(s"failure: $failure"))
  }  
}
```

Use the constructor arguments to configure the engine.

```scala
new FeelEngine(configuration = Configuration(externalFunctionsEnabled = true))
```

</TabItem>
<TabItem value="java">

Use the builder to create a new instance of the class `FeelEngine`.

```java
 public class MyProgram {

    public static void main(String[] args) {

        final FeelEngine engine = new FeelEngine.Builder()
            .valueMapper(SpiServiceLoader.loadValueMapper())
            .functionProvider(SpiServiceLoader.loadFunctionProvider())
            .build();

        final Map<String, Object> variables = Map.of("x", 21);
        final Either<FeelEngine.Failure, Object> result = engine.evalExpression(expression, variables);

        if (result.isRight()) {
            final Object value = result.right().get();
            System.out.println("result is " + value);
        } else {
            final FeelEngine.Failure failure = result.left().get();
            throw new RuntimeException(failure.message());
        }
    }
}
```

Use the builder to configure the engine.

```java
new FeelEngine.Builder().enableExternalFunctions(true).build()
```

</TabItem>
</Tabs>


:::danger Security
External functions are disabled by default. They would allow calling arbitrary
code or accessing sensitive data. It is recommended to use the
[FunctionProvider API](spi.md) instead.
:::

### Use as script engine

Calling the FEEL engine via Java's script engine
API ([JSR 223](https://www.jcp.org/en/jsr/detail?id=223)).

```scala
object MyProgram {

  val scriptEngineManager = new ScriptEngineManager
 
  def feel(script: String, context: ScriptContext) {
  
    val scriptEngine: FeelScriptEngine = scriptEngineManager.getEngineByName("feel")
    
    val result: Object = scriptEngine.eval(script, context)
    // ...
  }

}
```

The engine is registered under the following names:

* `feel`
* `http://www.omg.org/spec/FEEL/20140401` (FEEL namespace)
* `feel-scala`

To evaluate a unary-tests expression, use one of the following names:

* `feel-unary-tests`
* `feel-scala-unary-tests`
