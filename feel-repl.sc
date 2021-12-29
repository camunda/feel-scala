// import the FEEL engine library
import $ivy.`org.camunda.feel:feel-engine:1.14.0`, org.camunda.feel._

// import a logging library
import $ivy.`org.apache.logging.log4j:log4j-slf4j-impl:2.14.0`,
org.apache.logging.log4j.core.config.Configurator,
org.apache.logging.log4j.Level
// and enable the logging to print warnings
Configurator.setRootLevel(Level.WARN)

// initialize the FEEL engine
val feelEngine = new FeelEngine()

val feelEngineVersion = classOf[FeelEngine].getPackage.getImplementationVersion

// define a shortcut function for evaluation
def feel(expression: String, context: Map[String, Any] = Map.empty): Unit = {
  val evalResult = feelEngine.evalExpression(expression, context)
  printResult(evalResult)
}

def feel(expression: String, jsonContext: String): Any = {
  parseJsonObject(jsonContext)
    .map(context => feel(expression, context))
}

def unaryTests(expression: String,
               inputValue: Any,
               context: Map[String, Any] = Map.empty): Unit = {
  val contextWithInputValue = context + (FeelEngine.UnaryTests.defaultInputVariable -> inputValue)
  val evalResult = feelEngine.evalUnaryTests(expression, contextWithInputValue)
  printResult(evalResult)
}

def unaryTests(expression: String,
               jsonInputValue: String,
               jsonContext: String): Unit = {
  parseJsonObject(jsonContext).map { context =>
    val inputValue = parseJson(jsonInputValue)
    unaryTests(expression, inputValue, context)
  }
}

private def printResult(evalResult: Either[FeelEngine.Failure, Any]): Unit = {
  evalResult match {
    case Right(result) => println(fansi.Color.LightGreen(s"> $result"))
    case Left(failure) => println(fansi.Color.LightRed(s"> $failure"))
  }
}

private def parseJsonObject(json: String): Option[Map[String, Any]] = {
  parseJson(json) match {
    case objectValue: Map[String, Any] => Some(objectValue)
    case otherValue =>
      println(
        fansi.Color.LightRed(
          s"> Expected a JSON object as variables but found '$otherValue'"))
      None
  }
}

private def parseJson(json: String): Any = {
  val jsonValue = ujson.read(json)
  unpackJson(jsonValue)
}

private def unpackJson(json: ujson.Value): Any = {
  json match {
    case ujson.Obj(objValue) =>
      objValue.map { case (key, value) => key -> unpackJson(value) }.toMap
    case ujson.Arr(arrValue) => arrValue.map(unpackJson).toList
    case primitiveValue      => primitiveValue.value
  }
}

// print on loading the script
println(
  fansi.Color.LightBlue(s"===== FEEL Engine REPL ($feelEngineVersion) ======"))
println(
  fansi.Color.LightBlue(
    """> Evaluate FEEL expressions using 'feel("1 + 3")'"""))
println(
  fansi.Color.LightBlue(
    """> Evaluate FEEL unary-tests using 'unaryTests("[2..5]", 4)'"""))
println(
  fansi.Color.LightBlue(
    """> Provide variables as Map using 'feel("x + 3", Map("x" -> 2))'"""))
println(fansi.Color.LightBlue(
  """> Provide variables as JSON string using 'feel("x + 3", "{ \"x\" : 2 }")'"""))

// usage: amm --predef feel-repl.sc
