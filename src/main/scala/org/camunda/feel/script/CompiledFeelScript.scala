package org.camunda.feel.script

import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.Bindings
import javax.script.ScriptEngine
import org.camunda.feel.FeelEngine
import org.camunda.feel.parser.Exp

case class CompiledFeelScript(engine: FeelScriptEngine, val expression: Exp) extends CompiledScript {
  
  def getEngine: ScriptEngine = engine
  
  def eval(context: ScriptContext): Object = engine.eval(this, context)
  
}