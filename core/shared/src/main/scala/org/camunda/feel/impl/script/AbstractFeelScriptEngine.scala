package org.camunda.feel.impl.script

import fastparse.Parsed
import org.camunda.feel.FeelEngine
import org.camunda.feel.FeelEngine.EvalExpressionResult
import org.camunda.feel.syntaxtree.Exp

import javax.script.{ScriptContext, ScriptEngineFactory, ScriptException}
import scala.jdk.CollectionConverters._

trait AbstractFeelScriptEngine {
  def eval: (String, Map[String, Any]) => EvalExpressionResult

  def parse: String => Parsed[Exp]

  def factory: ScriptEngineFactory

  def engine: FeelEngine


  def eval(script: String, context: ScriptContext): Object = {
    val engineContext = getEngineContext(context)
    val result        = eval(script, engineContext)

    handleEvaluationResult(result)
  }


  protected def handleEvaluationResult(result: EvalExpressionResult): Object =
    result match {
      case Right(value)  => value.asInstanceOf[AnyRef]
      case Left(failure) => throw new ScriptException(failure.message)
    }

  protected def getEngineContext(context: ScriptContext): Map[String, Any] = {
    List(ScriptContext.GLOBAL_SCOPE, ScriptContext.ENGINE_SCOPE)
      .flatMap(scope => Option(context.getBindings(scope)))
      .flatMap(_.asScala)
      .toMap
  }

}
