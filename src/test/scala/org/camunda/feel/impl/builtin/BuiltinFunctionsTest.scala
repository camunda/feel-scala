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
package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.EvaluationResultMatchers
import org.camunda.feel.impl.FeelEngineTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** @author
  *   Philipp
  */
class BuiltinFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A built-in function" should "return null if arguments doesn't match" in {

    evaluateExpression(
      expression = "date(true)"
    ) should returnNull()

    evaluateExpression(
      expression = "number(false)"
    ) should returnNull()
  }

  "A not() function" should "negate Boolean" in {

    evaluateExpression(
      expression = "not(true)"
    ) should returnResult(false)

    evaluateExpression(
      expression = "not(false)"
    ) should returnResult(true)
  }

  "A is defined() function" should "return true if the value is present" in {
    evaluateExpression(
      expression = "is defined(1)"
    ) should returnResult(true)

    evaluateExpression(
      expression = "is defined(true)"
    ) should returnResult(true)

    evaluateExpression(
      expression = "is defined([])"
    ) should returnResult(true)

    evaluateExpression(
      expression = "is defined({})"
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is defined( {"a":1}.a ) """
    ) should returnResult(true)
  }

  it should "return false if the value is null" in {
    evaluateExpression(
      expression = "is defined(null)"
    ) should returnResult(false)
  }

  it should "return false if a variable doesn't exist" in {
    evaluateExpression(
      expression = "is defined(a)"
    ) should returnResult(false)

    evaluateExpression(
      expression = "is defined(a.b)"
    ) should returnResult(false)
  }

  it should "return false if a context entry doesn't exist" in {
    evaluateExpression(
      expression = "is defined({}.a)"
    ) should returnResult(false)

    evaluateExpression(
      expression = "is defined({}.a.b)"
    ) should returnResult(false)
  }

  "A get or else(value: Any, default: Any) function" should "return the value if not null" in {

    evaluateExpression(
      expression = "get or else(3, 1)"
    ) should returnResult(3)

    evaluateExpression(
      expression = """get or else("value", "default")"""
    ) should returnResult("value")

    evaluateExpression(
      expression = "get or else(value:3, default:1)"
    ) should returnResult(3)
  }

  it should "return the default param if value is null" in {

    evaluateExpression(
      expression = "get or else(null, 1)"
    ) should returnResult(1)

    evaluateExpression(
      expression = """get or else(null, "default")"""
    ) should returnResult("default")

    evaluateExpression(
      expression = "get or else(value:null, default:1)"
    ) should returnResult(1)
  }

  it should "return null if both value and default params are null" in {

    evaluateExpression(
      expression = "get or else(null, null)"
    ) should returnNull()

    evaluateExpression(
      expression = "get or else(value:null, default:null)"
    ) should returnNull()
  }

  "A assert(value: Any, condition: Any) function" should "return the value if the condition evaluated to true" in {

    evaluateExpression(
      expression = """assert(x, x > 3)""",
      variables = Map("x" -> 4)
    ) should returnResult(4)

    evaluateExpression(
      expression = """assert(x, x != null)""",
      variables = Map("x" -> "value")
    ) should returnResult("value")

    evaluateExpression(
      expression = """assert(x, x = 3)""",
      variables = Map("x" -> 3)
    ) should returnResult(3)

    evaluateExpression(
      expression = """assert(value: x, condition: x = 3)""",
      variables = Map("x" -> 3)
    ) should returnResult(3)
  }

  it should "fail the evaluation if the condition is evaluated to false" in {

    evaluateExpression(
      expression = """assert(x, x > 5)""",
      variables = Map("x" -> 4)
    ) should failWith("The condition is not fulfilled")

    evaluateExpression(
      expression = """assert(x, x != null)"""
    ) should failWith("The condition is not fulfilled")

    evaluateExpression(
      expression = """assert(x, x > 5)""",
      variables = Map("x" -> null)
    ) should failWith("The condition is not fulfilled")

    evaluateExpression(
      expression = """assert(x, x = 5)""",
      variables = Map("x" -> 4)
    ) should failWith("The condition is not fulfilled")

    evaluateExpression(
      expression = """list contains(assert(my_list, my_list != null), 2)"""
    ) should failWith(
      "Assertion failure on evaluate the expression 'list contains(assert(my_list, my_list != null), 2)': The condition is not fulfilled"
    )
  }

  "A assert(value: Any, condition: Any, cause: String) function" should "return the value if the condition evaluated to true" in {

    evaluateExpression(
      expression = """assert(x, x > 3, "The condition is not true")""",
      variables = Map("x" -> 4)
    ) should returnResult(4)

    evaluateExpression(
      expression = """assert(x, x != null, "The condition is not true")""",
      variables = Map("x" -> "value")
    ) should returnResult("value")

    evaluateExpression(
      expression = """assert(x, x = 3, "The condition is not true")""",
      variables = Map("x" -> 3)
    ) should returnResult(3)

    evaluateExpression(
      expression = """assert(value: x, condition: x = 3, cause: "The condition is not true")""",
      variables = Map("x" -> 3)
    ) should returnResult(3)
  }

  it should "fail the evaluation with custom message if the condition is evaluated to false" in {

    evaluateExpression(
      expression = """assert(x, x > 5, "The condition is not true")""",
      variables = Map("x" -> 4)
    ) should failWith("The condition is not true")

    evaluateExpression(
      expression = """assert(x, x != null, "The condition is not true")"""
    ) should failWith("The condition is not true")

    evaluateExpression(
      expression = """assert(x, x > 5, "The condition is not true")""",
      variables = Map("x" -> null)
    ) should failWith("The condition is not true")

    evaluateExpression(
      expression = """assert(x, x = 5, "The condition is not true")""",
      variables = Map("x" -> 4)
    ) should failWith("The condition is not true")

    evaluateExpression(
      expression =
        """list contains(assert(my_list, my_list != null, "The condition is not true"), 2)"""
    ) should failWith(
      "Assertion failure on evaluate the expression 'list contains(assert(my_list, my_list != null, \"The condition is not true\"), 2)': The condition is not true"
    )
  }

  "A is blank() function" should "return Boolean" in {
    evaluateExpression(
      expression = """ is blank("") """
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is blank(" ") """
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is blank("hello world") """
    ) should returnResult(false)

    evaluateExpression(
      expression = """ is blank(" hello world ") """
    ) should returnResult(false)

    evaluateExpression(
      expression = """ is blank("\t\n\r\f") """
    ) should returnResult(true)
  }

}
