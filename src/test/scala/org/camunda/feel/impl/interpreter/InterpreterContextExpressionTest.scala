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

/** @author
  *   Philipp Ossler
  */
class InterpreterContextExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A context" should "access previous entries within the same context" in {
    evaluateExpression("{a:1, b:a+1, c:b+1}") should returnResult(
      Map("a" -> 1, "b" -> 2, "c" -> 3)
    )
  }

  it should "access previous entries of outer context" in {
    evaluateExpression("{a:1, b:{c:a+2}}") should returnResult(
      Map("a" -> 1, "b" -> Map("c" -> 3))
    )
  }

  it should "not override variables of nested context" in {
    evaluateExpression("{ a:1, b:{ a:2, c:a+3 } }") should returnResult(
      Map("a" -> 1, "b" -> Map("a" -> 2, "c" -> 5))
    )
  }

  it should "access a previous entry if there is a variable with the same name (static context)" in {
    evaluateExpression(
      expression = "{a:1, b:a+1}",
      variables = Map("a" -> 0)
    ) should returnResult(
      Map("a" -> 1, "b" -> 2)
    )
  }

  it should "access a previous entry if there is a variable with the same name (custom context)" in {
    evaluateExpression(
      expression = "{a:1, b:a+1}",
      context = new MyCustomContext(Map("a" -> 0))
    ) should returnResult(
      Map("a" -> 1, "b" -> 2)
    )
  }

  it should "be compared with '='" in {
    evaluateExpression("{} = {}") should returnResult(true)
    evaluateExpression("{x:1} = {x:1}") should returnResult(true)
    evaluateExpression("{x:{ y:1 }} = {x:{ y:1 }}") should returnResult(true)

    evaluateExpression("{} = {x:1}") should returnResult(false)
    evaluateExpression("{x:1} = {}") should returnResult(false)
    evaluateExpression("{x:1} = {x:2}") should returnResult(false)
    evaluateExpression("{x:1} = {y:1}") should returnResult(false)

    evaluateExpression("{x:1} = {x:true}") should returnResult(false)
  }

  it should "be compared with '!='" in {
    evaluateExpression("{} != {}") should returnResult(false)
    evaluateExpression("{x:1} != {x:1}") should returnResult(false)
    evaluateExpression("{x:{ y:1 }} != {x:{ y:1 }}") should returnResult(false)

    evaluateExpression("{} != {x:1}") should returnResult(true)
    evaluateExpression("{x:1} != {}") should returnResult(true)
    evaluateExpression("{x:1} != {x:2}") should returnResult(true)
    evaluateExpression("{x:1} != {y:1}") should returnResult(true)

    evaluateExpression("{x:1} != {x:true}") should returnResult(true)
  }

  it should "be accessed and compared" in {
    evaluateExpression("{x:1}.x = 1") should returnResult(true)
  }

  it should "fail if compare to not a context" in {
    evaluateExpression("{} = 1") should failWith("expect Context but found 'ValNumber(1)'")
  }

  it should "fail when special symbols violate context syntax" in {
    evaluateExpression("{foo{bar:1}.`foo{bar` = 1") should failToParse()

    evaluateExpression("{foo,bar:1}.`foo,bar` = 1") should failToParse()

    evaluateExpression("{foo:bar:1}.`foo:bar` = 1") should failToParse()
  }

  "A context path expression" should "return the value (with literal)" in {
    evaluateExpression("{a:1}.a") should returnResult(1)
  }

  it should "return the value (with variable)" in {
    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> Map("b" -> 1))
    ) should returnResult(1)
  }

  it should "return a nested context" in {
    evaluateExpression("{a: {b:1}}.a") should returnResult(
      Map("b" -> 1)
    )
  }

  it should "return the value of the nested context" in {
    evaluateExpression("{a: {b:1}}.a.b") should returnResult(1)
  }

  it should "return the value of a previous nested context entry" in {
    evaluateExpression("{a:{b:1}, c:a.b+1}") should returnResult(
      Map("a" -> Map("b" -> 1), "c" -> 2)
    )
  }

  it should "fail if the context is empty" in {
    evaluateExpression("{}.x") should failWith("context contains no entry with key 'x'")
  }

  it should "fail if no entry exists with the key" in {
    evaluateExpression("{x:1, y:2}.z") should failWith("context contains no entry with key 'z'")
  }

  it should "return fail if the context is null" in {
    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> null)
    ) should failWith("No property found with name 'b' of value 'ValNull'")
  }

  it should "fail if the chained context is null" in {
    evaluateExpression("{a:1}.b.c") should failWith("context contains no entry with key 'b'")
  }

  it should "fail if the context is empty (inside a context)" in {
    evaluateExpression("{x:1, y:{}.z}") should failWith("context contains no entry with key 'z'")
  }

  it should "return the value of a key with whitespaces" in {
    evaluateExpression("{foo bar:1}.`foo bar`") should returnResult(1)

    evaluateExpression("{foo   bar:2}.`foo   bar`") should returnResult(2)

    evaluateExpression("{foo bar:3, fizz buzz: 4}.`fizz buzz`") should returnResult(4)
  }

  it should "return the value of a key with special symbols" in {
    evaluateExpression(
      expression = "{foo+bar:1}.`foo+bar`"
    ) should returnResult(1)

    evaluateExpression(
      expression = "{foo+bar:1, simple_special++char:4}.`simple_special++char`"
    ) should returnResult(4)

    evaluateExpression(
      expression = """{\uD83D\uDC0E:"\uD83D\uDE00"}.`\uD83D\uDC0E`"""
    ) should returnResult("\uD83D\uDE00")

    evaluateExpression(
      expression =
        "{ friend+of+mine:2, hello_there:{ how_are_you?:2, are_you_happy?:`friend+of+mine`+3 } }.hello_there.`are_you_happy?`"
    ) should returnResult(5)
  }

  "A context projection" should "contain the value of each entry (with literal)" in {
    evaluateExpression("[ {a:1, b:2}, {a:3, b:4} ].a") should returnResult(
      List(1, 3)
    )
  }

  it should "contain the value of each entry (with variable)" in {
    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> List(Map("b" -> 1), Map("b" -> 2)))
    ) should returnResult(List(1, 2))
  }

  "A context filter" should "access a context entry by key" in {
    evaluateExpression("[ {a:1, b:2}, {a:3, b:4} ][a > 2]") should returnResult(
      List(Map("a" -> 3, "b" -> 4))
    )
  }

  it should "access a context entry by the key 'item'" in {
    evaluateExpression("[ {item:1}, {item:2}, {item:3} ][item >= 2]") should returnResult(
      List(Map("item" -> 2), Map("item" -> 3))
    )
  }

  it should "be followed by a path expression" in {
    evaluateExpression("[{a:1}, {a:2}][1].a") should returnResult(1)
  }

  it should "be applied to a path expression" in {
    evaluateExpression("[ {a:1, b:2}, {a:3, b:4} ].a[1]") should returnResult(1)
  }

  it should "fail if it doesn't have the given key" in {
    evaluateExpression("[{x: 1, y: 2}, {x: 3}][y > 1]") should failWith("no variable found for name 'y'")
  }

  it should "not contain an entry if it the value is null" in {
    evaluateExpression("[{x: 1}, {x: null}][x > 0]") should returnResult(
      List(Map("x" -> 1))
    )
  }

  it should "contain all entries with null value" in {
    evaluateExpression("[{x: 1}, {x: null}][x = null]") should returnResult(
      List(Map("x" -> null))
    )
  }

  it should "contain all entries with null value or missing context entry" in {
    // note that a missing entry is equivalent to that entry containing null
    evaluateExpression("[{x: 1}, {y: 1}][x = null]") should returnResult(
      List(Map("y" -> 1))
    )
  }

}
