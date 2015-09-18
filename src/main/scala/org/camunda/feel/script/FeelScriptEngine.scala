package org.camunda.feel.script

import javax.script.ScriptEngine
import javax.script.Bindings
import java.io.Reader
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory
import javax.script.AbstractScriptEngine
import javax.script.SimpleBindings

class FeelScriptEngine extends AbstractScriptEngine with ScriptEngine {
  
  lazy val factory = new FeelScriptEngineFactory
  
  // TODO alternatives?
  def createBindings(): Bindings = new SimpleBindings

  def eval(reader: Reader, context: ScriptContext): Object = {
    ???
  }

  def eval(script: String, context: ScriptContext): Object = {
    ???
  }

  def getFactory(): ScriptEngineFactory = factory
  
}