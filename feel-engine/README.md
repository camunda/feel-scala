# FEEL Engine

A parser and interpreter for FEEL (Friendly Enough Expression Language) written in Scala. 

## How to use it?

Add the FEEL engine to your project by copying the [jar file](https://github.com/camunda/feel-scala/releases) _(feel-engine-${VERSION}-complete.jar)_ or adding the project as dependency.

```xml
<dependency>
  <groupId>org.camunda.bpm.extension.feel.scala</groupId>
  <artifactId>feel-engine</artifactId>
  <version>${VERSION}</version>
</dependency>
```

It is recommended to choose the complete jar file which includes all dependencies (e.g. Scala libraries).

### Native Way

Create a new instance of the class 'FeelEngine'. Use this instance to parse and evaluate a given expression or unary tests. 

```scala
object MyProgram {
  
  val engine = new FeelEngine
  
  def feel(expression: String, context: Map[String, Any]) {
    
    val result: EvalResult = engine.evalSimpleUnaryTests(expression, context)
    
    result match {
      case EvalValue(value)   =>  // ...
      case EvalFailure(error) =>  // ...
      case ParseFailure(error) => // ...
    }
  }  
}
```

### As Script Engine

The FEEL engine implements the Java script engine spi - [JSR 223](https://www.jcp.org/en/jsr/detail?id=223). 

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

It is registered by the names:

* `feel`
* `http://www.omg.org/spec/FEEL/20140401` (qualified name)
* `feel-scala`

You can also evaluate unary tests instead of an expression by using one of the names:

* `feel-unary-tests`
* `feel-scala-unary-tests`

## How to build it?

You can build the project with [SBT](http://www.scala-sbt.org) or [Maven](http://maven.apache.org).

### Using SBT

In the root directory:

Run the tests with
```
sbt engine/test
```

Build the single jar with
```
sbt engine/package
```

Build the complete jar including all dependencies with
```
sbt engine/assembly
```

### Using Maven

Run the tests with
```
mvn test
```

Build the jar including all dependencies with
```
mvn install
```
