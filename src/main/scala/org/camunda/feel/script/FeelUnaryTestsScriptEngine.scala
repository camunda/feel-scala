package org.camunda.feel.script

import java.io.Reader
import javax.script._
import scala.collection.JavaConversions._
import org.camunda.feel._
import scala.util.parsing.input.StreamReader
import java.io.IOException
import java.io.Closeable
import scala.annotation.tailrec
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.parser.FeelParser

class FeelUnaryTestsScriptEngine(val factory: ScriptEngineFactory) extends FeelScriptEngine {

  val eval = (expression: String, context: Map[String, Any]) => engine.evalSimpleUnaryTests(expression, context)
  
  val parse = (expression: String) => FeelParser.parseSimpleUnaryTests(expression)
  
}