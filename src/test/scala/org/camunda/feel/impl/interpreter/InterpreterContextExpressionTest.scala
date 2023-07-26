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

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest, FeelIntegrationTest}
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * @author Philipp Ossler
  */
class InterpreterContextExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A context" should "be filtered in a list" in {

    val list = eval("[ {a:1, b:2}, {a:3, b:4} ][a > 2]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items.head

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(3), "b" -> ValNumber(4)))
  }

  it should "be filtered via comparison with missing entry" in {

    val list = eval("[{x: 1, y: 2}, {x: 3}][y > 1]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items.head

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("x" -> ValNumber(1), "y" -> ValNumber(2)))
  }

  it should "be filtered via comparison with null value" in {
    val list = eval("[{x: 1}, {x: null}][x > 0]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items.head

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("x" -> ValNumber(1)))
  }

  it should "be filtered via matching null comparison" in {
    val list = eval("[{x: 1}, {x: null}][x = null]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items.head

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("x" -> ValNull))
  }

  // note that a missing entry is equivalent to that entry containing null
  it should "be filtered via missing entry null comparison" in {
    val list = eval("[{x: 1}, {y: 1}][x = null]")
    list shouldBe a[ValList]

    val items = list.asInstanceOf[ValList].items
    items should have size 1
    val context = items.head

    context
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("y" -> ValNumber(1)))
  }

  it should "be filtered by name 'item'" in {

    eval("[ {item:1}, {item:2}, {item:3} ][item >= 2]") match {
      case ValList(List(ValContext(context1), ValContext(context2))) =>
        context1.variableProvider.getVariables should be(
          Map("item" -> ValNumber(2)))
        context2.variableProvider.getVariables should be(
          Map("item" -> ValNumber(3)))

      case actual => fail(s"expected a list with two items but found '$actual'")
    }
  }

  it should "be accessed and filtered in a list" in {

    eval("[ {a:1, b:2}, {a:3, b:4} ].a[1]") should be(ValNumber(1))
  }

  it should "access previous entries within the same context" in {
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

    eval("{ a:1, b:{ a:2, c:a+3 } }.b.c") should be(ValNumber(5))
  }

  it should "be filtered and accessed" in {
    eval("[{a:1}, {a:2}][1].a") should be(ValNumber(1))
  }

  it should "be compared with '='" in {

    eval("{} = {}") should be(ValBoolean(true))
    eval("{x:1} = {x:1}") should be(ValBoolean(true))
    eval("{x:{ y:1 }} = {x:{ y:1 }}") should be(ValBoolean(true))

    eval("{} = {x:1}") should be(ValBoolean(false))
    eval("{x:1} = {}") should be(ValBoolean(false))
    eval("{x:1} = {x:2}") should be(ValBoolean(false))
    eval("{x:1} = {y:1}") should be(ValBoolean(false))

    eval("{x:1} = {x:true}") should be(ValBoolean(false))
  }

  it should "be compared with '!='" in {

    eval("{} != {}") should be(ValBoolean(false))
    eval("{x:1} != {x:1}") should be(ValBoolean(false))
    eval("{x:{ y:1 }} != {x:{ y:1 }}") should be(ValBoolean(false))

    eval("{} != {x:1}") should be(ValBoolean(true))
    eval("{x:1} != {}") should be(ValBoolean(true))
    eval("{x:1} != {x:2}") should be(ValBoolean(true))
    eval("{x:1} != {y:1}") should be(ValBoolean(true))

    eval("{x:1} != {x:true}") should be(ValBoolean(true))
  }

  it should "be accessed and compared" in {
    eval("{x:1}.x = 1") should be(ValBoolean(true))
  }

  it should "fail to compare if not a context" in {

    eval("{} = 1") should be(
      ValError("expect Context but found 'ValNumber(1)'")
    )
  }

  it should "fail when special symbols violate context syntax" in {
    eval("{foo{bar:1}.`foo{bar` = 1") shouldBe a[ValError]
    eval("{foo,bar:1}.`foo,bar` = 1") shouldBe a[ValError]
    eval("{foo:bar:1}.`foo:bar` = 1") shouldBe a[ValError]
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

  it should "return null if the context is empty" in {
    evaluateExpression("{}.x") should (
      returnNull() and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'x'. The context is empty"
        )
      )
  }

  it should "return null if no entry exists with the key" in {
    evaluateExpression("{x:1, y:2}.z") should (
      returnNull() and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'z'. Available keys: x, y"
        )
      )
  }

  it should "return null if the context is null" in {
    evaluateExpression(
      expression = "a.b",
      variables = Map("a" -> null)
    ) should (
      returnNull() and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'b'. The context is null")
      )
  }

  it should "return null if the chained context is null" in {
    evaluateExpression("{a:1}.b.c") should (
      returnNull() and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'b'. Available keys: a") and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'c'. The context is null")
    )
  }

  it should "return null if the context is empty (inside a context)" in {
    evaluateExpression("{x:1, y:{}.z}") should (
      returnResult(Map("x" -> 1, "y" -> null)) and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'z'. The context is empty"
        )
      )
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
      expression = "{ friend+of+mine:2, hello_there:{ how_are_you?:2, are_you_happy?:`friend+of+mine`+3 } }.hello_there.`are_you_happy?`"
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

  it should "contain null if a context doesn't have the given key" in {
    evaluateExpression("[ {a:1}, {b:2} ].a") should (
      returnResult(List(1, null)) and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'a'. Available keys: b")
      )

    evaluateExpression("[ {a:1}, {b:2} ].b") should (
      returnResult(List(null, 2)) and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'b'. Available keys: a")
      )

    evaluateExpression("[ {a:1}, {b:2} ].c") should (
      returnResult(List(null, null)) and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'c'. Available keys: a") and
        reportFailure(
          failureType = EvaluationFailureType.NO_CONTEXT_ENTRY_FOUND,
          failureMessage = "No context entry found with key 'c'. Available keys: b")
      )
  }

}
