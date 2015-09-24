# FEEL 
A parser and interpreter for FEEL (friendly enough expression language) written in scala. 

FEEL is a part of the (DMN Spec)[http://www.omg.org/spec/DMN/] and is designed to write expressions and conditions in a simple way what can easily understand by business professionals and developers.
S-FEEL is a subset of FEEL that can be used for simple expressions and conditions. Mostly, it should used for decision tables.

## Goal
Currently, the goal of the parser and interpreter is to support S-FEEL, so that it can used for decision tables.

## Supported Language Features
* data types: number, string, boolean, date
* simple unary tests: compare operators ('<', '<=', '>', '>=', equal), interval, negation, combination of multiple tests
* qualified names

## Current Limitations
* unsupported data types: time, duration
* no simple expressions
* the input of the simple unary test should be given as context entry 'cellInput'

# How to use 
The parser and interpreter can be integrated as dependency or as jar which include all dependencies (size of ~ 7mb).
Make sure you have build and deploy it (locally) before. It is not deployed in a public repository yet.

## Native Way
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

## As Script Engine
The spec (JSR 223)[https://www.jcp.org/en/jsr/detail?id=223] is implemented so that it can be used as script engine. It is registered by name 'feel'.

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

# How to build

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

# Examples
Just to have a feeling how an expression can look like:

## Simple Unary Test
```
< 42

(2..4)

2,4

not(2,4)

>= a

[date("2015-09-17")..date("2015-09-19")]

"good"

```
