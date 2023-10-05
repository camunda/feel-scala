/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.impl.script

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import javax.script.ScriptContext
import javax.script.SimpleScriptContext
import javax.script.ScriptException

/** @author
  *   Philipp Ossler
  */
class UnaryTestsScriptEngineTest extends AnyFlatSpec with Matchers {

  val scriptEngine = new FeelUnaryTestsScriptEngine(new FeelUnaryTestsScriptEngineFactory)

  "The feel unary tests script engine" should "get the script engine factory" in {

    scriptEngine.getFactory shouldBe a[FeelUnaryTestsScriptEngineFactory]
  }

  it should "evaluate a simpleUnaryTest script '< 3'" in {

    val context  = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("< 2", context).asInstanceOf[Boolean] should be(false)
    eval("< 3", context).asInstanceOf[Boolean] should be(true)
  }

  it should "evaluate a simpleUnaryTest script 'not(3,4)'" in {

    val context  = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("not(2,3)", context).asInstanceOf[Boolean] should be(false)
    eval("not(3,4)", context).asInstanceOf[Boolean] should be(true)
  }

  it should "compile and evaluate an expression '< 3'" in {

    val compiledScript = scriptEngine.compile("< 3")

    compiledScript should not be (null)

    val context  = new SimpleScriptContext
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
