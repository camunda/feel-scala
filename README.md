# FEEL 
A parser and interpreter for FEEL (friendly enough expression language) written in scala. 

FEEL is a part of the [DMN Spec](http://www.omg.org/spec/DMN/) and is designed to write expressions and conditions in a simple way what can easily understand by business professionals and developers.
S-FEEL is a subset of FEEL that can be used for simple expressions and conditions. Mostly, it should used for decision tables.

## Supported Language Features
* data types: 
  * number
  * string
  * boolean
  * date
  * time
  * duration
* simple unary tests: 
  * compare operators ('<', '<=', '>', '>=', equal)
  * interval
  * negation
  * combination of multiple tests with ','
* expression:
  * arithmetic expression ('+', '-', '*', '/', '**', negation) (numbers only)
  * if-then-else
  * disjunction and conjunction
  * comparison (compare operators, 'between x and y', 'x in y') 
  * function definition (no external)
  * function invocation (positional + named parameters)
  * simple positive unary test (as expression)

## Current Limitations
* the input of the simple unary test should be given as context entry 'cellInput'
* limited support for types of input and variables

## How to use 
The parser and interpreter can be integrated as dependency (requires Scala in classpath) or as jar which include all dependencies (size of ~ 7mb).

Make sure you have build and deploy it (locally) before. It is not deployed in a public repository yet. 

Or just use a built jar of the released version found on [github release page](https://github.com/saig0/feel/releases).

### Native Way
There is a class 'FeelEngine' that can be used to parse and evaluate a given expression. This class can be called from a scala or java program. 

```scala
object AnyProgram {
  
  val engine = new FeelEngine
  
  def feel(expression: String, context: Map[String, Any]) {
    
    val result: EvalResult = engine.evalSimpleUnaryTest(expression, context)
    
    result match {
      case EvalValue(value)   =>  // ...
      case EvalFailure(error) =>  // ...
      case ParseFailure(error) => // ...
    }
  }
  
}
```

### As Script Engine
The spec [JSR 223](https://www.jcp.org/en/jsr/detail?id=223) is implemented so that it can be used as script engine. It is registered by name 'feel'.

```scala
object AnyProgram {

  val scriptEngineManager = new ScriptEngineManager
 
  def feel(script: String, context: ScriptContext) {
  
    val scriptEngine: FeelScriptEngine = scriptEngineManager.getEngineByName("feel")
    
    val result: Object = scriptEngine.eval(script, context)
    // ...
  }

}
```

## How to build

> Requirements
* [SBT](http://www.scala-sbt.org) to build and test the application

Run the tests with
```
sbt test
```

Build the jar including all dependencies with
```
sbt assemply
```

Deploy it locally with
```
sbt pulishLocal
```

## Examples
Just to have a feeling how an expression can look like:

### Simple Unary Test
```
< 42                                        // input less than 42

(2..4)                                      // input greater than 2 and less than 4

2,4                                         // input is 2 or 4

not(2,4)                                    // input is not 2 or 4

>= a                                        // input is greater or equal to the value of variable 'a'

"good"                                      // input is equal the string 'good'

[date("2015-09-17")..date("2015-09-19")]    // input is after or equal '2015-09-17' and before or equal '2015-09-19'

]time("08:00:00")..time("16:00:00")[        // input is after '08:00:00' and before '16:00:00'

<= duration("P1D")                          // input is less or equal to 'P1D' (one day)

```

## Using the FEEL Engine to evaluate DMN decision tables
Camunda provide a [DMN Engine](https://github.com/camunda/camunda-engine-dmn) what can evaluate DMN decision tables. You can use the FEEL engine inside of the DMN engine to evaluate expressions. See the [integration](https://github.com/saig0/camunda-feel-integration) for details.
