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

import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.camunda.feel.syntaxtree._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
  *   Philipp
  */
class BuiltinStringFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A substring() function" should "return string with _ characters" in {

    evaluateExpression(""" substring("foobar",3) """) should returnResult("obar")
  }

  it should "return string with _ characters starting at _" in {

    evaluateExpression(""" substring("foobar",3,3) """) should returnResult("oba")
  }

  it should "return string with _ characters starting at negative _" in {

    evaluateExpression(""" substring("foobar",-2,1) """) should returnResult("a")
  }

  it should "be invoked with named parameters" in {
    evaluateExpression(""" substring(string: "foobar", start position:3) """) should returnResult(
      "obar"
    )
  }

  it should "return string with remaining characters if the length is greater than the string" in {
    evaluateExpression(""" substring("abc", 1, 4) """) should returnResult("abc")
    evaluateExpression(""" substring("abc", 2, 4) """) should returnResult("bc")
    evaluateExpression(""" substring("abc", -1, 4) """) should returnResult("c")
    evaluateExpression(""" substring("abc", 4, 4) """) should returnResult("")
  }

  "A string length() function" should "return the length of a String" in {

    evaluateExpression(""" string length("foo") """) should returnResult(3)
  }

  "A upper case() function" should "return uppercased String" in {

    evaluateExpression(""" upper case("aBc4") """) should returnResult("ABC4")
  }

  "A lower case() function" should "return lowercased String" in {

    evaluateExpression(""" lower case("aBc4") """) should returnResult("abc4")
  }

  "A substring before() function" should "return substring before match" in {

    evaluateExpression(""" substring before("foobar", "bar") """) should returnResult("foo")

    evaluateExpression(""" substring before("foobar", "xyz") """) should returnResult("")
  }

  "A substring after() function" should "return substring after match" in {

    evaluateExpression(""" substring after("foobar", "ob") """) should returnResult("ar")

    evaluateExpression(""" substring after("", "a") """) should returnResult("")

    evaluateExpression(""" substring after("foo", "") """) should returnResult("foo")
  }

  "A replace() function" should "replace a String" in {

    evaluateExpression(""" replace("abcd", "(ab)|(a)", "[1=$1][2=$2]") """) should returnResult(
      "[1=ab][2=]cd"
    )
  }

  it should "replace a String with regex pattern" in (evaluateExpression(
    """ replace("0123456789", "(\d{3})(\d{3})(\d{4})", "($1) $2-$3") """
  ) should returnResult("(012) 345-6789"))

  it should "return null if the pattern is invalid" in {
    evaluateExpression(""" replace("abc", "([a-z)", "$1") """) should returnNull()
  }

  "A contains() function" should "return if contains the match" in {

    evaluateExpression(""" contains("foobar", "ob") """) should returnResult(true)

    evaluateExpression(""" contains("foobar", "of") """) should returnResult(false)
  }

  "A starts with() function" should "return if starts with match" in {

    evaluateExpression(""" starts with("foobar", "fo") """) should returnResult(true)

    evaluateExpression(""" starts with("foobar", "ba") """) should returnResult(false)
  }

  "A ends with() function" should "return if ends with match" in {

    evaluateExpression(""" ends with("foobar", "r") """) should returnResult(true)

    evaluateExpression(""" ends with("foobar", "o") """) should returnResult(false)
  }

  "A matches() function" should "return if String matches a pattern" in {

    evaluateExpression(""" matches("foobar", "^fo*b") """) should returnResult(true)

    evaluateExpression(""" matches("foobar", "^fo*z") """) should returnResult(false)
  }

  it should "return null if the pattern is invalid" in {
    evaluateExpression(""" matches("abc", "[a-z") """) should returnNull()
  }

  "A split() function" should "return a list of substrings" in {

    evaluateExpression(""" split("John Doe", "\s") """) should returnResult(
      List("John", "Doe")
    )

    evaluateExpression(""" split("a;b;c;;", ";") """) should returnResult(
      List("a", "b", "c", "", "")
    )
  }

  "An extract() function" should "return a list of strings matching a pattern" in {

    evaluateExpression(
      """ extract("this is foobar and folbar", "fo[a-z]*") """
    ) should returnResult(
      List("foobar", "folbar")
    )

    evaluateExpression(""" extract("nothing", "fo[a-z]*") """) should returnResult(List())

    evaluateExpression(""" extract("This is fobbar!", "fo[a-z]*") """) should returnResult(
      List("fobbar")
    )
  }

  it should "return null if the pattern is invalid" in {
    evaluateExpression(""" extract("abc", "[a-z") """) should returnNull()
  }

  "A trim() function" should "return the eliminates leading and trailing spaces of a String" in {

    evaluateExpression(""" trim("hello world") """) should returnResult("hello world")

    evaluateExpression(""" trim("hello world  ") """) should returnResult("hello world")

    evaluateExpression(""" trim("  hello world") """) should returnResult("hello world")

    evaluateExpression(""" trim("  hello world  ") """) should returnResult("hello world")

    evaluateExpression(""" trim(" hello   world ") """) should returnResult("hello   world")
  }

  "A uuid() function" should "return a string" in {

    evaluateExpression(" uuid() ").result shouldBe a[String]
  }

  it should "return a string of length 36" in {

    evaluateExpression(" string length(uuid()) ") should returnResult(36)
  }

  "A to base64() function" should "return a string encoded as base64" in {

    evaluateExpression(""" to base64("FEEL") """) should returnResult("RkVFTA==")

    evaluateExpression(""" to base64(value: "Camunda") """) should returnResult("Q2FtdW5kYQ==")
  }

  "A is blank() function" should "return true if the string contains only whitespace" in {
    evaluateExpression(
      expression = """ is blank("") """
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is blank(" ") """
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is blank("\t\n\r\f") """
    ) should returnResult(true)

    evaluateExpression(
      expression = """ is blank(string: "") """
    ) should returnResult(true)
  }

  it should "return false if the string contains only non-whitespace characters" in {
    evaluateExpression(
      expression = """ is blank("hello world") """
    ) should returnResult(false)

    evaluateExpression(
      expression = """ is blank(" hello world ") """
    ) should returnResult(false)
  }

  "A ksuid() function" should "return a string" in {

    evaluateExpression(" ksuid() ").result shouldBe a[String]
  }

  it should "return a string of length 27" in {

    evaluateExpression(" string length(ksuid()) ") should returnResult(27)
  }
}
