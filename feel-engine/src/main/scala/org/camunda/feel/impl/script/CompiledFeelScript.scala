package org.camunda.feel.impl.script

import javax.script.{CompiledScript, ScriptContext, ScriptEngine}
import org.camunda.feel.syntaxtree.ParsedExpression

case class CompiledFeelScript(engine: FeelScriptEngine,
                              val expression: ParsedExpression)
    extends CompiledScript {

  def getEngine: ScriptEngine = engine

  def eval(context: ScriptContext): Object = engine.eval(this, context)

}
