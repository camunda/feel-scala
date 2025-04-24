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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{LocalDate, LocalTime, ZoneId, ZonedDateTime}
import javax.script.{ScriptContext, ScriptException, SimpleScriptContext}

/** @author
  *   Philipp Ossler
  */
class ScriptEngineTest extends AnyFlatSpec with Matchers {

  val scriptEngine = new FeelExpressionScriptEngine(new FeelScriptEngineFactory)

  "The feel script engine" should "get the script engine factory" in {

    scriptEngine.getFactory shouldBe a[FeelScriptEngineFactory]
  }

  it should "evaluate a simpleUnaryTest script '< 3' using engine scope" in {

    val context  = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    eval("< 2", context).asInstanceOf[Boolean] should be(false)
    eval("< 3", context).asInstanceOf[Boolean] should be(true)
  }

  it should "evaluate a simpleUnaryTest script '< 3' using global scope" in {

    val context  = new SimpleScriptContext
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

  it should "compile and evaluate an expression '< 3'" in {

    val compiledScript = scriptEngine.compile("< 3")

    compiledScript should not be (null)

    val context  = new SimpleScriptContext
    val bindings = scriptEngine.createBindings()
    bindings.put("cellInput", 2)
    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

    compiledScript.eval(context).asInstanceOf[Boolean] should be(true)
  }

  it should "throw an exception when parse an invalid script" in {

    a[ScriptException] should be thrownBy eval("? 3", new SimpleScriptContext)
  }

  it should "throw an exception when compile an invalid script" in {

    a[ScriptException] should be thrownBy scriptEngine.compile("? 3")
  }

  it should "be extend by a custom function provider" in {

    val context = new SimpleScriptContext

    eval("foo(2)", context) should be(3)
  }

  it should "be configured by a custom value mapper" in {

    val context = new SimpleScriptContext

    eval("null", context) should be("foobar")
  }

  it should "be configured by a custom clock" in {

    val now = ZonedDateTime.of(
      LocalDate.parse("2020-07-31"),
      LocalTime.parse("14:27:30"),
      ZoneId.of("Europe/Berlin")
    )

    PinnedClock.currentTime = now

    eval("now()", new SimpleScriptContext) should be(now)
  }

  private def eval(script: String, context: ScriptContext) =
    scriptEngine.eval(script, context)

}
