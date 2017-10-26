package org.camunda.feel.script

import javax.script._
import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

class FeelUnaryTestsScriptEngine(val factory: ScriptEngineFactory) extends FeelScriptEngine {

  val eval = (expression: String, context: Map[String, Any]) => engine.evalUnaryTests(expression, context)

  val parse = (expression: String) => FeelParser.parseUnaryTests(expression)

}
