package org.camunda.feel.impl.script

import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.Bindings
import javax.script.ScriptEngine
import org.camunda.feel.impl.FeelEngine
import org.camunda.feel.impl.parser.Exp
import org.camunda.feel.impl.ParsedExpression
import org.camunda.feel.impl.ParsedExpression

case class CompiledFeelScript(engine: FeelScriptEngine,
                              val expression: ParsedExpression)
    extends CompiledScript {

  def getEngine: ScriptEngine = engine

  def eval(context: ScriptContext): Object = engine.eval(this, context)

}
