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
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.double2bigDecimal
import scala.math.BigDecimal.int2bigDecimal

/**
  * @author Philipp
  */
class BuiltinNumberFunctionsTest
    extends FlatSpec
    with Matchers
    with FeelIntegrationTest {

  "A decimal() function" should "return number with a given scale" in {

    eval(" decimal((1/3), 2) ") should be(ValNumber(0.33))
    eval(" decimal(1.5, 0) ") should be(ValNumber(2))
    eval(" decimal(2.5, 0) ") should be(ValNumber(2))
  }

  "A decimal() function with rounding mode" should "return number with a given scale" in {

    eval(""" decimal(1.5, 0, "HALF_EVEN") """) should be(ValNumber(2))
    eval(""" decimal(1.5, 0, "HALF_UP") """) should be(ValNumber(2))
    eval(""" decimal(1.5, 0, "UP") """) should be(ValNumber(2))
    eval(""" decimal(1.5, 0, "HALF_DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.5, 0, "DOWN") """) should be(ValNumber(1))
    eval(""" decimal(1.5, 0, "FLOOR") """) should be(ValNumber(1))
    eval(""" decimal(1.5, 0, "CEILING") """) should be(ValNumber(2))
  }

  "A floor() function" should "return greatest integer <= _" in {

    eval(" floor(1.5) ") should be(ValNumber(1))
    eval(" floor(-1.5) ") should be(ValNumber(-2))
  }

  "A ceiling() function" should "return smallest integer >= _" in {

    eval(" ceiling(1.5) ") should be(ValNumber(2))
    eval(" ceiling(-1.5) ") should be(ValNumber(-1))
  }

  "A abs() function" should "return absolute value" in {

    eval(" abs(10) ") should be(ValNumber(10))
    eval(" abs(-10) ") should be(ValNumber(10))
  }

  "A modulo() function" should "return the remainder of the division of dividend by divisor" in {

    eval(" modulo(12, 5) ") should be(ValNumber(2))
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

  "A even() function" should "return true if number is even" in {

    eval(" even(5) ") should be(ValBoolean(false))
    eval(" even(2) ") should be(ValBoolean(true))
  }

}
