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

import org.camunda.feel.FeelEngine.UnaryTests
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.camunda.feel.syntaxtree._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp Ossler
  */
class InterpreterExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "An expression" should "be an if-then-else (with parentheses)" in {
    val exp = """ if (x < 5) then "low" else "high" """

    evaluateExpression(exp, Map("x" -> 2)) should returnResult("low")
    evaluateExpression(exp, Map("x" -> 7)) should returnResult("high")

    evaluateExpression(exp, Map("x" -> "foo")) should returnResult("high")
  }

  it should "be an if-then-else (without parentheses)" in {
    evaluateExpression("if x < 5 then 1 else 2", Map("x" -> 2)) should returnResult(1)
  }

  it should "be an if-then-else (with literal)" in {
    evaluateExpression("if true then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with path)" in {
    evaluateExpression("if {a: true}.a then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with filter)" in {
    evaluateExpression("if [true][1] then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with conjunction)" in {
    evaluateExpression("if true and true then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with disjunction)" in {
    evaluateExpression("if false or true then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with in-test)" in {
    evaluateExpression("if 1 in < 5 then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with instance of)" in {
    evaluateExpression("if 1 instance of number then 1 else 2") should returnResult(1)
  }

  it should "be an if-then-else (with variable and function call -> then)" in {
    evaluateExpression(
      "if 7 > var then flatten(xs) else []",
      Map("xs" -> List(1, 2), "var" -> 3)
    ) should returnResult(List(1, 2))
  }

  it should "be an if-then-else (with variable and function call -> else)" in {
    evaluateExpression(
      "if false then var else flatten(xs)",
      Map("xs" -> List(1, 2), "var" -> 3)
    ) should returnResult(List(1, 2))
  }

  it should "be a simple positive unary test" in {

    evaluateExpression("< 3", Map(UnaryTests.defaultInputVariable -> 2)) should returnResult(true)

    evaluateExpression("(2 .. 4)", Map(UnaryTests.defaultInputVariable -> 5)) should returnResult(
      false
    )
  }

  it should "be an instance of (literal)" in {

    evaluateExpression("x instance of number", Map("x" -> 1)) should returnResult(true)
    evaluateExpression("x instance of number", Map("x" -> "NaN")) should returnResult(false)

    evaluateExpression("x instance of boolean", Map("x" -> true)) should returnResult(true)
    evaluateExpression("x instance of boolean", Map("x" -> 0)) should returnResult(false)

    evaluateExpression("x instance of string", Map("x" -> "yes")) should returnResult(true)
    evaluateExpression("x instance of string", Map("x" -> 0)) should returnResult(false)
  }

  it should "be an instance of (duration)" in {
    evaluateExpression(
      """duration("P3M") instance of years and months duration"""
    ) should returnResult(true)
    evaluateExpression(
      """duration("PT4H") instance of days and time duration"""
    ) should returnResult(true)
    evaluateExpression("""null instance of years and months duration""") should returnResult(false)
    evaluateExpression("""null instance of days and time duration""") should returnResult(false)
  }

  it should "be an instance of (date)" in {
    evaluateExpression("""date("2023-03-07") instance of date""") should returnResult(true)
    evaluateExpression(""" @"2023-03-07" instance of date""") should returnResult(true)
    evaluateExpression("1 instance of date") should returnResult(false)
  }

  it should "be an instance of (time)" in {
    evaluateExpression("""time("11:27:00") instance of time""") should returnResult(true)
    evaluateExpression(""" @"11:27:00" instance of time""") should returnResult(true)
    evaluateExpression("1 instance of time") should returnResult(false)
  }

  it should "be an instance of (date and time)" in {
    evaluateExpression(
      """date and time("2023-03-07T11:27:00") instance of date and time"""
    ) should returnResult(true)

    evaluateExpression(""" @"2023-03-07T11:27:00" instance of date and time""") should returnResult(
      true
    )
    evaluateExpression("1 instance of date and time") should returnResult(false)
  }

  it should "be an instance of (list)" in {
    evaluateExpression("[1,2,3] instance of list") should returnResult(true)
    evaluateExpression("[] instance of list") should returnResult(true)
    evaluateExpression("1 instance of list") should returnResult(false)
  }

  it should "be an instance of (context)" in {
    evaluateExpression("{x:1} instance of context") should returnResult(true)
    evaluateExpression("{} instance of context") should returnResult(true)
    evaluateExpression("1 instance of context") should returnResult(false)
  }

  it should "be an instance of (multiplication)" in {
    evaluateExpression("2 * 3 instance of number") should returnResult(true)
  }

  it should "be an instance of (function definition)" in {
    evaluateExpression(""" (function() "foo") instance of function """) should returnResult(true)
    evaluateExpression("""1 instance of function""") should returnResult(false)
  }

  it should "be a instance of Any should always pass" in {
    evaluateExpression("x instance of Any", Map("x" -> "yes")) should returnResult(true)
    evaluateExpression("x instance of Any", Map("x" -> 1)) should returnResult(true)
    evaluateExpression("x instance of Any", Map("x" -> true)) should returnResult(true)
    evaluateExpression("x instance of Any", Map("x" -> null)) should returnResult(false)
  }

  it should "be an escaped identifier" in {
    // regular identifier
    evaluateExpression(" `x` ", Map("x" -> "foo")) should returnResult("foo")
    // with whitespace
    evaluateExpression(" `a b` ", Map("a b" -> "foo")) should returnResult("foo")
    // with operator
    evaluateExpression(" `a-b` ", Map("a-b" -> 3)) should returnResult(3)
  }

  it should "contains parentheses" in {
    evaluateExpression("(1 + 2)") should returnResult(3)
    evaluateExpression("(1 + 2) + 3") should returnResult(6)
    evaluateExpression("1 + (2 + 3)") should returnResult(6)

    evaluateExpression("([1,2,3])[1]") should returnResult(1)
    evaluateExpression("({x:1}).x") should returnResult(1)
    evaluateExpression("{x:(1)}.x") should returnResult(1)

    evaluateExpression("[1,2,3,4][(1)]") should returnResult(1)
  }

  it should "contain parentheses in a context literal" in {
    val context = Map("xs" -> List(1, 2, 3))

    evaluateExpression("{x:(xs[1])}.x", context) should returnResult(1)
    evaluateExpression("{x:(xs)[1]}.x", context) should returnResult(1)
    evaluateExpression("{x:(xs)}.x", context) should returnResult(List(1, 2, 3))
  }

  it should "contains nested filter expressions" in {
    evaluateExpression("[1,2,3,4][item > 2][1]") should returnResult(3)
    evaluateExpression("([1,2,3,4])[item > 2][1]") should returnResult(3)
    evaluateExpression("([1,2,3,4][item > 2])[1]") should returnResult(3)
  }

  it should "contains nested path expressions" in {
    evaluateExpression("{x:{y:1}}.x.y") should returnResult(1)
    evaluateExpression("{x:{y:{z:1}}}.x.y.z") should returnResult(1)

    evaluateExpression("({x:{y:{z:1}}}).x.y.z") should returnResult(1)
    evaluateExpression("({x:{y:{z:1}}}.x).y.z") should returnResult(1)
    evaluateExpression("({x:{y:{z:1}}}.x.y).z") should returnResult(1)
  }

  it should "contains nested filter and path expressions" in {
    evaluateExpression("[{x:{y:1}},{x:{y:2}},{x:{y:3}}].x.y[2]") should returnResult(2)
    evaluateExpression("([{x:{y:1}},{x:{y:2}},{x:{y:3}}]).x.y[2]") should returnResult(2)
    evaluateExpression("([{x:{y:1}},{x:{y:2}},{x:{y:3}}].x).y[2]") should returnResult(2)
    evaluateExpression("([{x:{y:1}},{x:{y:2}},{x:{y:3}}].x.y)[2]") should returnResult(2)

    evaluateExpression("([{x:{y:1}},{x:{y:2}},{x:{y:3}}]).x[2].y") should returnResult(2)
    evaluateExpression("([{x:{y:1}},{x:{y:2}},{x:{y:3}}])[2].x.y") should returnResult(2)

    evaluateExpression("[{x:[1,2]},{x:[3,4]},{x:[5,6]}][2].x[1]") should returnResult(3)

    evaluateExpression("([{x:[1,2]},{x:[3,4]},{x:[5,6]}]).x[2][1]") should returnResult(3)
    evaluateExpression("([{x:[1,2]},{x:[3,4]},{x:[5,6]}].x)[2][1]") should returnResult(3)
    evaluateExpression("([{x:[1,2]},{x:[3,4]},{x:[5,6]}].x[2])[1]") should returnResult(3)
  }

  "Null" should "compare to null" in {

    evaluateExpression("null = null") should returnResult(true)
    evaluateExpression("null != null") should returnResult(false)
  }

  it should "compare to nullable variable" in {

    evaluateExpression("null = x", Map("x" -> ValNull)) should returnResult(true)
    evaluateExpression("null = x", Map("x" -> 1)) should returnResult(false)

    evaluateExpression("null != x", Map("x" -> ValNull)) should returnResult(false)
    evaluateExpression("null != x", Map("x" -> 1)) should returnResult(true)
  }

  it should "compare to nullable context entry" in {

    evaluateExpression("null = {x: null}.x") should returnResult(true)
    evaluateExpression("null = {x: 1}.x") should returnResult(false)

    evaluateExpression("null != {x: null}.x") should returnResult(false)
    evaluateExpression("null != {x: 1}.x") should returnResult(true)
  }

  it should "compare to not existing variable" in {

    evaluateExpression("null = x") should returnResult(true)
    evaluateExpression("null = x.y") should returnResult(true)

    evaluateExpression("x = null") should returnResult(true)
    evaluateExpression("x.y = null") should returnResult(true)
  }

  it should "compare to not existing context entry" in {

    evaluateExpression("null = {}.x") should returnResult(true)
    evaluateExpression("null = {x: null}.x.y") should returnResult(true)

    evaluateExpression("{}.x = null") should returnResult(true)
    evaluateExpression("{x: null}.x.y = null") should returnResult(true)
  }

  "A variable name" should "not be a key-word" in {
    evaluateExpression("{ null: 1 }.null") should failToParse()
    evaluateExpression("{ true: 1}.true") should failToParse()
    evaluateExpression("{ false: 1}.false") should failToParse()
    evaluateExpression("function") should failToParse()
    evaluateExpression("in") should failToParse()
    evaluateExpression("return") should failToParse()
    evaluateExpression("then") should failToParse()
    evaluateExpression("else") should failToParse()
    evaluateExpression("satisfies") should failToParse()
    evaluateExpression("and") should failToParse()
    evaluateExpression("or") should failToParse()
  }

//  Ignored as these keywords are not listed as reserved keywords yet
  ignore should "not be a key-word (ignored)" in {
    evaluateExpression("some") should failToParse()
    evaluateExpression("every") should failToParse()
    evaluateExpression("if") should failToParse()
    evaluateExpression("for") should failToParse()
    evaluateExpression("between") should failToParse()
    evaluateExpression("instance") should failToParse()
    evaluateExpression("of") should failToParse()
    evaluateExpression("not") should failToParse()
  }

  List(
    "something",
    "everything",
    "often",
    "orY",
    "andX",
    "trueX",
    "falseY",
    "nullOrString",
    "functionX",
    "instances",
    "forDev",
    "ifImportant",
    "thenX",
    "elseY",
    "betweenXandY",
    "notThis",
    "inside",
    "durationX",
    "dateX",
    "timeX"
  ).foreach { variableName =>
    it should s"contain a key-word ($variableName)" in {

      evaluateExpression(s"$variableName = true", Map(variableName -> true)) should returnResult(
        true
      )
    }
  }

  "A comment" should "be written as end of line comments //" in {
    evaluateExpression(""" [1,2,3][1] // the first item """) should returnResult(1)
  }

  it should "be written as trailing comments /* .. */" in {
    evaluateExpression(""" [1,2,3][1] /* the first item */ """) should returnResult(1)
  }

  it should "be written as single line comments /* .. */" in {
    evaluateExpression("""
        /* the first item */
        [1,2,3][1]
        """) should returnResult(1)
  }

  it should "be written as block comments /* .. */" in {
    evaluateExpression("""
        /*
         * the first item
         */
        [1,2,3][1]
        """) should returnResult(1)
  }

}
