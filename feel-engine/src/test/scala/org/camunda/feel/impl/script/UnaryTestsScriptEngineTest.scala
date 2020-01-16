package org.camunda.feel.impl.script

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import javax.script.ScriptContext
import javax.script.SimpleScriptContext
import javax.script.Bindings
import javax.script.ScriptException

/**
  * @author Philipp Ossler
  */
class UnaryTestsScriptEngineTest extends FlatSpec with Matchers {

  val scriptEngine = new FeelUnaryTestsScriptEngine(
    new FeelUnaryTestsScriptEngineFactory)

  "The feel unary tests script engine" should "get the script engine factory" in {

    scriptEngine.getFactory shouldBe a[FeelUnaryTestsScriptEngineFactory]
  }

  it should "evaluate a simpleUnaryTest script '< 3'" in {

    val context = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("< 2", context).asInstanceOf[Boolean] should be(false)
    eval("< 3", context).asInstanceOf[Boolean] should be(true)
  }

  it should "evaluate a simpleUnaryTest script 'not(3,4)'" in {

    val context = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("not(2,3)", context).asInstanceOf[Boolean] should be(false)
    eval("not(3,4)", context).asInstanceOf[Boolean] should be(true)
  }

  it should "compile and evaluate an expression '< 3'" in {

    val compiledScript = scriptEngine.compile("< 3")

    compiledScript should not be (null)

    val context = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    compiledScript.eval(context).asInstanceOf[Boolean] should be(true)
  }

  it should "throw an exception when parse an expression" in {

    a[ScriptException] should be thrownBy eval("3 + 4", new SimpleScriptContext)
  }

  it should "throw an exception when compile an invalid script" in {

    a[ScriptException] should be thrownBy scriptEngine.compile("? 3")
  }

  private def eval(script: String, context: ScriptContext) =
    scriptEngine.eval(script, context)

}
