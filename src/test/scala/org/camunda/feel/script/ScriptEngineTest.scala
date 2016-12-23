package org.camunda.feel.script

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import javax.script.ScriptContext
import javax.script.SimpleScriptContext
import javax.script.Bindings
import javax.script.ScriptException

/**
 * @author Philipp Ossler
 */
class ScriptEngineTest extends FlatSpec with Matchers {

  val scriptEngine = new FeelScriptEngine

  "The feel script engine" should "get the script engine factory" in {

    scriptEngine.getFactory shouldBe a[FeelScriptEngineFactory]
  }

  it should "evaluate a simpleUnaryTest script '< 3' using engine scope" in {

    val context = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("< 2", context).asInstanceOf[Boolean] should be(false)
    eval("< 3", context).asInstanceOf[Boolean] should be(true)
  }

  it should "evaluate a simpleUnaryTest script '< 3' using global scope" in {

    val context = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE)

    eval("< 2", context).asInstanceOf[Boolean] should be(false)
    eval("< 3", context).asInstanceOf[Boolean] should be(true)
  }
  
  it should "evaluate an expression script '2 + 3'" in {

    val context = new SimpleScriptContext

    eval("2 + 3", context) should be(5)
  }

  it should "throw an exception when parse an invalid script" in {

    a[ScriptException] should be thrownBy eval("? 3", new SimpleScriptContext)
  }
  
  it should "throw an exception when input is missing" in {
    
    a[ScriptException] should be thrownBy eval("< 3", new SimpleScriptContext)
  }

  private def eval(script: String, context: ScriptContext) = scriptEngine.eval(script, context)
  
}