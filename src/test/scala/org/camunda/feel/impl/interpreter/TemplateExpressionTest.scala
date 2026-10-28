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
package org.camunda.feel.impl.interpreter

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class TemplateExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A template" should "be a simple string" in {

    evaluateExpression("```Hello friend!```") should returnResult("Hello friend!")
  }

  it should "contain a variable reference" in {

    evaluateExpression(
      expression = "```Hello {{name}}```",
      variables = Map("name" -> "Zee")
    ) should returnResult("Hello Zee")

    evaluateExpression(
      expression = "```Hello {{name}}, nice to meet you.```",
      variables = Map("name" -> "Zee")
    ) should returnResult("Hello Zee, nice to meet you.")
  }

  it should "contain an expression" in {

    evaluateExpression(
      expression = """```Good {{if hour < 12 then "morning" else "afternoon"}}```""",
      variables = Map("hour" -> 9)
    ) should returnResult("Good morning")

    evaluateExpression(
      expression = """```Good {{if hour < 12 then "morning" else "afternoon"}}```""",
      variables = Map("hour" -> 15)
    ) should returnResult("Good afternoon")
  }

  it should "insert the value as a string" in {

    evaluateExpression(
      expression = """```Value: {{value}}```""",
      variables = Map("value" -> "FEEL")
    ) should returnResult("Value: FEEL")

    evaluateExpression(
      expression = """```Value: {{value}}```""",
      variables = Map("value" -> 123)
    ) should returnResult("Value: 123")

    evaluateExpression(
      expression = """```Value: {{value}}```""",
      variables = Map("value" -> true)
    ) should returnResult("Value: true")

    evaluateExpression(
      expression = """```Value: {{value}}```""",
      variables = Map("value" -> List(1, 2, 3))
    ) should returnResult("Value: [1, 2, 3]")

    evaluateExpression(
      expression = """```Value: {{value}}```""",
      variables = Map("value" -> Map("a" -> 1, "b" -> 2))
    ) should returnResult("Value: {a:1, b:2}")
  }

  it should "contain an conditional section" in {

    evaluateExpression(
      expression = """```Hello{{#if name = "Zee"}} my friend{{/if}}!```""",
      variables = Map("name" -> "Zee")
    ) should returnResult("Hello my friend!")

    evaluateExpression(
      expression = """```Hello{{#if name = "Zee"}} my friend{{/if}}!```""",
      variables = Map("name" -> "Joe")
    ) should returnResult("Hello!")
  }

  it should "contain a loop section" in {

    evaluateExpression(
      expression = """```Items:\n{{#loop items}}- {{this}}\n{{/loop}}```""",
      variables = Map("items" -> List("a", "b", "c"))
    ) should returnResult("""Items:
        |- a
        |- b
        |- c
        |""".stripMargin)
  }

}
