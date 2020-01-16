package org.camunda.feel.impl.script

import javax.script._
import org.camunda.feel.impl._
import org.camunda.feel.impl.parser.FeelParser
import org.camunda.feel.impl.parser.FeelParser

class FeelUnaryTestsScriptEngine(val factory: ScriptEngineFactory)
    extends FeelScriptEngine {

  val eval = (expression: String, context: Map[String, Any]) =>
    engine.evalUnaryTests(expression, context)

  val parse = (expression: String) => FeelParser.parseUnaryTests(expression)

}
