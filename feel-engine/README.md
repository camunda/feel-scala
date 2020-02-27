# FEEL Engine

The core FEEL engine (parser and interpreter). Usually, it is integrated in a project as library. In the project, the engine is either called directly (i.e. `engine.evalExpression(..)`) or using the script engine API. 

## Usage

Add the engine as dependency to your project:

```xml
<dependency>
  <groupId>org.camunda.feel</groupId>
  <artifactId>feel-engine</artifactId>
  <version>${VERSION}</version>
</dependency>
```

Or copy the [jar file](https://github.com/camunda/feel-scala/releases) _(feel-engine-${VERSION}-complete.jar)_ directly.

### Standalone

Create a new instance of the class 'FeelEngine'. Use this instance to parse and evaluate a given expression or unary tests. 

Using Scala:

```scala
object MyProgram {
  
  val engine = new FeelEngine
  
  def feel(expression: String, context: Map[String, Any]) {
    
    val result: Either[Failure, Boolean] = engine.evalSimpleUnaryTests(expression, context)
    // or    
    val result: Either[Failure, Any] = engine.evalExpression(expression, context)
  
    // handle result
    result
        .right.map(value => println(s"result is: $value"))
        .left.map(failure => println(s"failure: $failure"))
  }  
}
```

Or using Java:

```java
 public class MyProgram {

    public static void main(String[] args) {

        final FeelEngine engine = new FeelEngine.Builder()
            .valueMapper(SpiServiceLoader.loadValueMapper())
            .functionProvider(SpiServiceLoader.loadFunctionProvider())
            .build();

        final Map<String, Object> variables = Collections.singletonMap("x", 21);
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

### Script Engine

Call the engine via Java script engine API [JSR 223](https://www.jcp.org/en/jsr/detail?id=223). 

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

The engine is registered under the names:

* `feel`
* `http://www.omg.org/spec/FEEL/20140401` (qualified name)
* `feel-scala`

You can also evaluate unary tests instead of an expression by using one of the names:

* `feel-unary-tests`
* `feel-scala-unary-tests`
