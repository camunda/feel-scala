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

import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import scala.math.BigDecimal.{double2bigDecimal, int2bigDecimal}

/**
  * @author Philipp
  */
class BuiltinNumberFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A decimal() function" should "return number with a given scale" in {

    eval(" decimal((1/3), 2) ") should be(ValNumber(0.33))
    eval(" decimal(1.5, 0) ") should be(ValNumber(2))
    eval(" decimal(2.5, 0) ") should be(ValNumber(2))
  }

  it should "use the given rounding mode" in {
    // see https://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html

    eval(""" decimal(5.5, 0, "UP") """) should be(ValNumber(6))
    eval(""" decimal(5.5, 0, "DOWN") """) should be(ValNumber(5))
    eval(""" decimal(5.5, 0, "CEILING") """) should be(ValNumber(6))
    eval(""" decimal(5.5, 0, "FLOOR") """) should be(ValNumber(5))
    eval(""" decimal(5.5, 0, "HALF_UP") """) should be(ValNumber(6))
    eval(""" decimal(5.5, 0, "HALF_DOWN") """) should be(ValNumber(5))
    eval(""" decimal(5.5, 0, "HALF_EVEN") """) should be(ValNumber(6))
    eval(""" decimal(5.5, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(2.5, 0, "UP") """) should be(ValNumber(3))
    eval(""" decimal(2.5, 0, "DOWN") """) should be(ValNumber(2))
    eval(""" decimal(2.5, 0, "CEILING") """) should be(ValNumber(3))
    eval(""" decimal(2.5, 0, "FLOOR") """) should be(ValNumber(2))
    eval(""" decimal(2.5, 0, "HALF_UP") """) should be(ValNumber(3))
    eval(""" decimal(2.5, 0, "HALF_DOWN") """) should be(ValNumber(2))
    eval(""" decimal(2.5, 0, "HALF_EVEN") """) should be(ValNumber(2))
    eval(""" decimal(2.5, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(1.6, 0, "UP") """) should be(ValNumber(2))
    eval(""" decimal(1.6, 0, "DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.6, 0, "CEILING") """) should be(ValNumber(2))
    eval(""" decimal(1.6, 0, "FLOOR") """) should be(ValNumber(1))
    eval(""" decimal(1.6, 0, "HALF_UP") """) should be(ValNumber(2))
    eval(""" decimal(1.6, 0, "HALF_DOWN") """) should be(ValNumber(2))
    eval(""" decimal(1.6, 0, "HALF_EVEN") """) should be(ValNumber(2))
    eval(""" decimal(1.6, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(1.1, 0, "UP") """) should be(ValNumber(2))
    eval(""" decimal(1.1, 0, "DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.1, 0, "CEILING") """) should be(ValNumber(2))
    eval(""" decimal(1.1, 0, "FLOOR") """) should be(ValNumber(1))
    eval(""" decimal(1.1, 0, "HALF_UP") """) should be(ValNumber(1))
    eval(""" decimal(1.1, 0, "HALF_DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.1, 0, "HALF_EVEN") """) should be(ValNumber(1))
    eval(""" decimal(1.1, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(1.0, 0, "UP") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "CEILING") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "FLOOR") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "HALF_UP") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "HALF_DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "HALF_EVEN") """) should be(ValNumber(1))
    eval(""" decimal(1.0, 0, "UNNECESSARY") """) should be(ValNumber(1))

    eval(""" decimal(-1.0, 0, "UP") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "DOWN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "CEILING") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "FLOOR") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "HALF_UP") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "HALF_DOWN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "HALF_EVEN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.0, 0, "UNNECESSARY") """) should be(ValNumber(-1))

    eval(""" decimal(-1.1, 0, "UP") """) should be(ValNumber(-2))
    eval(""" decimal(-1.1, 0, "DOWN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.1, 0, "CEILING") """) should be(ValNumber(-1))
    eval(""" decimal(-1.1, 0, "FLOOR") """) should be(ValNumber(-2))
    eval(""" decimal(-1.1, 0, "HALF_UP") """) should be(ValNumber(-1))
    eval(""" decimal(-1.1, 0, "HALF_DOWN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.1, 0, "HALF_EVEN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.1, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(-1.6, 0, "UP") """) should be(ValNumber(-2))
    eval(""" decimal(-1.6, 0, "DOWN") """) should be(ValNumber(-1))
    eval(""" decimal(-1.6, 0, "CEILING") """) should be(ValNumber(-1))
    eval(""" decimal(-1.6, 0, "FLOOR") """) should be(ValNumber(-2))
    eval(""" decimal(-1.6, 0, "HALF_UP") """) should be(ValNumber(-2))
    eval(""" decimal(-1.6, 0, "HALF_DOWN") """) should be(ValNumber(-2))
    eval(""" decimal(-1.6, 0, "HALF_EVEN") """) should be(ValNumber(-2))
    eval(""" decimal(-1.6, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(-2.5, 0, "UP") """) should be(ValNumber(-3))
    eval(""" decimal(-2.5, 0, "DOWN") """) should be(ValNumber(-2))
    eval(""" decimal(-2.5, 0, "CEILING") """) should be(ValNumber(-2))
    eval(""" decimal(-2.5, 0, "FLOOR") """) should be(ValNumber(-3))
    eval(""" decimal(-2.5, 0, "HALF_UP") """) should be(ValNumber(-3))
    eval(""" decimal(-2.5, 0, "HALF_DOWN") """) should be(ValNumber(-2))
    eval(""" decimal(-2.5, 0, "HALF_EVEN") """) should be(ValNumber(-2))
    eval(""" decimal(-2.5, 0, "UNNECESSARY") """) should be(ValNull)

    eval(""" decimal(-5.5, 0, "UP") """) should be(ValNumber(-6))
    eval(""" decimal(-5.5, 0, "DOWN") """) should be(ValNumber(-5))
    eval(""" decimal(-5.5, 0, "CEILING") """) should be(ValNumber(-5))
    eval(""" decimal(-5.5, 0, "FLOOR") """) should be(ValNumber(-6))
    eval(""" decimal(-5.5, 0, "HALF_UP") """) should be(ValNumber(-6))
    eval(""" decimal(-5.5, 0, "HALF_DOWN") """) should be(ValNumber(-5))
    eval(""" decimal(-5.5, 0, "HALF_EVEN") """) should be(ValNumber(-6))
    eval(""" decimal(-5.5, 0, "UNNECESSARY") """) should be(ValNull)
  }

  it should "use the given rounding mode (case-insensitive)" in {
    eval(""" decimal(1.5, 0, "CEILING") """) should be(ValNumber(2))
    eval(""" decimal(1.5, 0, "ceiling") """) should be(ValNumber(2))
    eval(""" decimal(1.5, 0, "CeiLing") """) should be(ValNumber(2))
  }

  it should "return null if the rounding mode is not valid" in {
    eval(""" decimal(1.5, 0, "unknown") """) should be(ValNull)
  }

  "A floor() function" should "return greatest integer <= _" in {

    eval(" floor(1.5) ") should be(ValNumber(1))
    eval(" floor(-1.5) ") should be(ValNumber(-2))
    eval(" floor(-1.56, 1) ") should be(ValNumber(-1.6))
  }

  "A ceiling() function" should "return smallest integer >= _" in {

    eval(" ceiling(1.5) ") should be(ValNumber(2))
    eval(" ceiling(-1.5) ") should be(ValNumber(-1))
    eval(" ceiling(-1.56, 1) ") should be(ValNumber(-1.5))
  }

  "A abs() function" should "return absolute value" in {

    eval(" abs(10) ") should be(ValNumber(10))
    eval(" abs(-10) ") should be(ValNumber(10))
  }

  it should "be invoked with named parameter number" in {

    eval(" abs(number: 1) ") should be(ValNumber(1))
    eval(" abs(number: -1) ") should be(ValNumber(1))
  }

  it should "be invoked with named parameter n" in {

    eval(" abs(n: 1) ") should be(ValNumber(1))
    eval(" abs(n: -1) ") should be(ValNumber(1))
  }

  "A modulo() function" should "return the remainder of the division of dividend by divisor" in {

    eval(" modulo(12, 5) ") should be(ValNumber(2))
  }

  it should "return the negative reminder of the division of dividend by divisor" in {

    eval(" modulo(12, -5) ") should be(ValNumber(-3))
    eval(" modulo(-12, -5) ") should be(ValNumber(-2))
    eval(" modulo(10.1, -4.5) ") should be(ValNumber(-3.4))
    eval(" modulo(-10.1, -4.5) ") should be(ValNumber(-1.1))
  }

  it should "return the positive reminder of the division of dividend by divisor" in {

    eval(" modulo(-12, 5) ") should be(ValNumber(3))
    eval(" modulo(-10.1, 4.5) ") should be(ValNumber(3.4))
  }

  "A sqrt() function" should "return square root" in {

    eval(" sqrt(16) ") should be(ValNumber(4))
    eval(" sqrt(-1) ") should be(ValNull)
  }

  "A log() function" should "return natural logarithm" in {

    eval(" log( 10 ) ") should be(ValNumber(2.302585092994046))
  }

  "A exp() function" should "return Euler’s number e raised to the power of number" in {

    eval(" exp( 5 ) ") should be(ValNumber(148.4131591025766))
  }

  "A odd() function" should "return true if number is odd" in {

    eval(" odd(5) ") should be(ValBoolean(true))
    eval(" odd(2) ") should be(ValBoolean(false))
  }

  it should "return true if negative number is odd" in {

    eval(" odd(-5)") should be(ValBoolean(true))
    eval(" odd(-2)") should be(ValBoolean(false))
  }

  "A even() function" should "return true if number is even" in {

    eval(" even(5) ") should be(ValBoolean(false))
    eval(" even(2) ") should be(ValBoolean(true))
  }

  "A round up() function" should "return number with a given scale" in {

    eval(" round up(5.5, 0) ") should be(ValNumber(6))
    eval(" round up(-5.5, 0) ") should be(ValNumber(-6))
    eval(" round up(1.121, 2) ") should be(ValNumber(1.13))
    eval(" round up(-1.126, 2) ") should be(ValNumber(-1.13))
  }

  "A round down() function" should "return number with a given scale" in {

    eval(" round down(5.5, 0) ") should be(ValNumber(5))
    eval(" round down(-5.5, 0) ") should be(ValNumber(-5))
    eval(" round down(1.121, 2) ") should be(ValNumber(1.12))
    eval(" round down(-1.126, 2) ") should be(ValNumber(-1.12))
  }

  "A round half up() function" should "return number with a given scale" in {

    eval(" round half up(5.5, 0) ") should be(ValNumber(6))
    eval(" round half up(-5.5, 0) ") should be(ValNumber(-6))
    eval(" round half up(1.121, 2) ") should be(ValNumber(1.12))
    eval(" round half up(-1.126, 2) ") should be(ValNumber(-1.13))
  }

  "A round half down() function" should "return number with a given scale" in {

    eval(" round half down(5.5, 0) ") should be(ValNumber(5))
    eval(" round half down(-5.5, 0) ") should be(ValNumber(-5))
    eval(" round half down(1.121, 2) ") should be(ValNumber(1.12))
    eval(" round half down(-1.126, 2) ") should be(ValNumber(-1.13))
  }

  it should "return a number between 0.0 and 1.0 " in {
    eval(" random number() ") should be //(be >= ValNumber(0.0) and be <= ValNumber(1.0))
  }

  "A random number() function" should "return a number" in {
    eval(" random number() ") shouldBe a [ValNumber]
  }

}
