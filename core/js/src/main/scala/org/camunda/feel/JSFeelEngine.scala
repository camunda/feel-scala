package org.camunda.feel

import org.camunda.feel.api.FeelEngineApi

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExportAll}
import org.camunda.feel.impl.interpreter.ObjectContext
import org.camunda.feel.api.SuccessfulEvaluationResult
import org.camunda.feel.api.FailedEvaluationResult
import org.camunda.feel.api.EvaluationResult
import org.camunda.feel.valuemapper.ValueMapper
import org.camunda.feel.impl.DefaultValueMapper
import org.camunda.feel.valuemapper.JSValueMapper

@JSExportAll
@JSExportTopLevel("JSFeelEngine")
class JSFeelEngine() {

  private val valueMapper = new JSValueMapper()
  private val engine      = new FeelEngine(valueMapper = valueMapper)

  private val api = new FeelEngineApi(engine)

  def evaluate(expression: String, context: js.Dynamic): EvaluationResult = {
    val ctx = ObjectContext(context)
    api.evaluateExpression(expression, ctx)
  }

  def evaluateUnaryTests(expression: String, context: js.Dynamic): EvaluationResult = {
    val ctx = ObjectContext(context)
    api.evaluateUnaryTests(expression, ctx)
  }
}
