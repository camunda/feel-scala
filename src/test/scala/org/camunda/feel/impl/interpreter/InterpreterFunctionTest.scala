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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/**
  * @author Philipp Ossler
  */
class InterpreterFunctionTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A function definition" should "be returned as a function" in {

    val function = eval("function(x) x + 1")

    function shouldBe a[ValFunction]
    function.asInstanceOf[ValFunction].params should be(List("x"))
  }

  "A function invocation" should "invoke a function without parameter" in {

    val functions =
      Map("f" -> eval("""function() "invoked" """).asInstanceOf[ValFunction])

    eval("f()", functions = functions) should be(ValString("invoked"))
  }

  it should "invoke a function with a positional parameter" in {

    val functions =
      Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(1)", functions = functions) should be(ValNumber(2))
    eval("f(2)", functions = functions) should be(ValNumber(3))
  }

  it should "invoke a function with positional parameters" in {

    val functions =
      Map("add" -> eval("function(x,y) x + y").asInstanceOf[ValFunction])

    eval("add(1,2)", functions = functions) should be(ValNumber(3))
    eval("add(2,3)", functions = functions) should be(ValNumber(5))
  }

  it should "invoke a function a named parameter" in {

    val functions =
      Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(x:1)", functions = functions) should be(ValNumber(2))
    eval("f(x:2)", functions = functions) should be(ValNumber(3))
  }

  it should "invoke a function with named parameters" in {

    val functions =
      Map("sub" -> eval("function(x,y) x - y").asInstanceOf[ValFunction])

    eval("sub(x:4,y:2)", functions = functions) should be(ValNumber(2))
    eval("sub(y:2,x:4)", functions = functions) should be(ValNumber(2))
  }

  it should "take an expression as parameter" in {

    val functions =
      Map("f" -> eval("function(x) x + 1").asInstanceOf[ValFunction])

    eval("f(2 + 3)", functions = functions) should be(ValNumber(6))
  }

  it should "take another function as parameter" in {

    val functions =
      Map("a" -> eval("function(x) x + 1").asInstanceOf[ValFunction],
          "b" -> eval("function(x) x + 2").asInstanceOf[ValFunction])

    eval("a(b(1))", functions = functions) should be(ValNumber(4))
  }

  it should "return null if invoked with wrong parameters" in {

    val functions =
      Map("f" -> eval("function(x,y) true").asInstanceOf[ValFunction])

    eval("f()", functions = functions) should be(ValNull)
    eval("f(1)", functions = functions) should be(ValNull)

    eval("f(x:1,z:3)", functions = functions) should be(ValNull)
    eval("f(x:1,y:2,z:3)", functions = functions) should be(ValNull)
  }

  it should "return null if no function exists with the name" in {
    eval("f()") should be(ValNull)
  }

  it should "return null if the name doesn't resolve to a function" in {
    eval("f()", variables = Map("x" -> "a variable")) should be(ValNull)
  }

  it should "replace not set parameters with null" in {

    val functions = Map("f" -> eval("""
      function(x,y)
        if x = null
        then "x"
        else if y = null
        then "y"
        else "ok"
        """).asInstanceOf[ValFunction])

    eval("f(x:1)", functions = functions) should be(ValString("y"))
    eval("f(y:1)", functions = functions) should be(ValString("x"))
    eval("f(x:1,y:1)", functions = functions) should be(ValString("ok"))
  }

  it should "be followed by a path" in {
    eval(""" date(2019,09,17).year """) should be(ValNumber(2019))
  }

  it should "be followed by a filter" in {
    eval(""" index of([1,2,3,2],2)[1]  """) should be(ValNumber(2))
  }

  it should "invoke a function with parameters contain whitespaces" in {
    eval("""number(from: "1.000.000,01", decimal separator:",", grouping separator:".")""") should be(ValNumber(1_000_000.01))
  }

  it should "invoke a function with a named parameter containing whitespaces" in {
    val functions =
      Map("f" -> eval("""function(test name) `test name` + 1""").asInstanceOf[ValFunction])

    eval("f(test name:1)", functions = functions) should be(ValNumber(2))
    eval("f(test name:2)", functions = functions) should be(ValNumber(3))
  }

  it should "invoke a function with a named parameter containing more than one whitespace" in {
    val functions =
      Map("f" -> eval("""function(test   name yada) `test   name yada` + 1""").asInstanceOf[ValFunction])

    eval("f(test   name yada:1)", functions = functions) should be(ValNumber(2))
    eval("f(test   name yada:2)", functions = functions) should be(ValNumber(3))
  }

  "An external Java function invocation" should "invoke a function with a double parameter" in {

    val functions = Map(
      "cos" -> eval(
        """ function(angle) external { java: { class: "java.lang.Math", method signature: "cos(double)" } } """)
        .asInstanceOf[ValFunction])

    eval("cos(0)", functions = functions) should be(ValNumber(1))
    eval("cos(1)", functions = functions) should be(ValNumber(Math.cos(1)))
  }

  it should "invoke a function with two int parameters" in {

    val functions = Map(
      "max" -> eval(
        """ function(x,y) external { java: { class: "java.lang.Math", method signature: "max(int, int)" } } """)
        .asInstanceOf[ValFunction])

    eval("max(1,2)", functions = functions) should be(ValNumber(2))
  }

  it should "invoke a function with a long parameters" in {

    val functions = Map(
      "abs" -> eval(
        """ function(a) external { java: { class: "java.lang.Math", method signature: "abs(long)" } } """)
        .asInstanceOf[ValFunction])

    eval("abs(-1)", functions = functions) should be(ValNumber(1))
  }

  it should "invoke a function with a float parameters" in {

    val functions = Map(
      "round" -> eval(
        """ function(a) external { java: { class: "java.lang.Math", method signature: "round(float)" } } """)
        .asInstanceOf[ValFunction])

    eval("round(3.2)", functions = functions) should be(ValNumber(3))
  }

}
