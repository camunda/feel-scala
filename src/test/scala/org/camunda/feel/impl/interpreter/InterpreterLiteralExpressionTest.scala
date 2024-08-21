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
import org.camunda.feel.syntaxtree._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time._

/** @author
  *   Philipp Ossler
  */
class InterpreterLiteralExpressionTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A literal" should "be a number" in {

    evaluateExpression("2") should returnResult(2)
    evaluateExpression("2.4") should returnResult(2.4)
    evaluateExpression("-3") should returnResult(-3)
    evaluateExpression("02") should returnResult(2)
    evaluateExpression("02.4") should returnResult(2.4)
    evaluateExpression("-03") should returnResult(-3)
    evaluateExpression("0000002") should returnResult(2)

  }

  it should "be a string" in {

    evaluateExpression(""" "a" """) should returnResult("a")
  }

  it should "be a boolean" in {

    evaluateExpression("true") should returnResult(true)
  }

  it should "be null" in {

    evaluateExpression("null") should returnNull()
  }

  it should "be a context (identifier as key)" in {

    evaluateExpression("{ a : 1 }") should returnResult(Map("a" -> 1))

    evaluateExpression("""{ a:1, b:"foo" }""") should returnResult(
      Map("a" -> 1, "b" -> "foo")
    )

    // nested
    evaluateExpression("{ a : { b : 1 } }") should returnResult(
      Map("a" -> Map("b" -> 1))
    )
  }

  it should "be a context (string as key)" in {
    evaluateExpression(""" {"a":1} """) should returnResult(Map("a" -> 1))
  }

  it should "be a list" in {

    evaluateExpression("[1]") should returnResult(List(1))

    evaluateExpression("[1,2]") should returnResult(List(1, 2))

    // nested
    evaluateExpression("[ [1], [2] ]") should returnResult(List(List(1), List(2)))
  }

  "A date literal" should "be defined" in {
    evaluateExpression(""" date("2021-09-08") """) should returnResult(
      LocalDate.parse("2021-09-08")
    )
  }

  it should "be defined with '@'" in {
    evaluateExpression(""" @"2021-09-08" """) should returnResult(
      LocalDate.parse("2021-09-08")
    )
  }

  it should "be null if the date is not valid (not a leap year)" in {
    evaluateExpression(""" date("2023-02-29") """) should returnNull()
    evaluateExpression(""" @"2023-02-29" """) should returnNull()
  }

  it should "be null if the date is not valid (month without 31 days)" in {
    evaluateExpression(""" date("2023-06-31") """) should returnNull()
    evaluateExpression(""" @"2023-06-31" """) should returnNull()
  }

  "A time literal" should "be defined without offset" in {
    evaluateExpression(""" time("10:30:00") """) should returnResult(
      LocalTime.parse("10:30:00")
    )
  }

  it should "be defined with offset" in {
    evaluateExpression(""" time("10:30:00+02:00") """) should returnResult(
      OffsetTime.parse("10:30:00+02:00")
    )
  }

  it should "be defined with timezone" in {
    evaluateExpression(""" time("10:30:00@Europe/Berlin") """) should returnResult(
      OffsetTime.parse("10:30:00+01:00")
    )
  }

  it should "be defined with '@' and no offset" in {
    evaluateExpression(""" @"10:30:00" """) should returnResult(
      LocalTime.parse("10:30:00")
    )
  }

  it should "be defined with '@' and offset" in {
    evaluateExpression(""" @"10:30:00+02:00" """) should returnResult(
      OffsetTime.parse("10:30:00+02:00")
    )
  }

  it should "be defined with '@' and timezone" in {
    evaluateExpression(""" @"10:30:00@Europe/Berlin" """) should returnResult(
      OffsetTime.parse("10:30:00+01:00")
    )
  }

  "A date-time literal" should "be defined without offset" in {
    evaluateExpression(""" date and time("2021-09-08T10:30:00") """) should returnResult(
      LocalDateTime.parse("2021-09-08T10:30:00")
    )
  }

  it should "be defined with offset" in {
    evaluateExpression(""" date and time("2021-09-08T10:30:00+02:00") """) should returnResult(
      ZonedDateTime.of(LocalDateTime.parse("2021-09-08T10:30:00"), ZoneOffset.ofHours(2))
    )
  }

  it should "be defined with timezone" in {
    evaluateExpression(
      """ date and time("2021-09-08T10:30:00@Europe/Berlin") """
    ) should returnResult(
      ZonedDateTime.of(
        LocalDateTime.parse("2021-09-08T10:30:00"),
        ZoneId.of("Europe/Berlin")
      )
    )
  }

  it should "be defined with '@' and no offset" in {
    evaluateExpression(""" @"2021-09-08T10:30:00" """) should returnResult(
      LocalDateTime.parse("2021-09-08T10:30:00")
    )
  }

  it should "be defined with '@' and offset" in {
    evaluateExpression(""" @"2021-09-08T10:30:00+02:00" """) should returnResult(
      ZonedDateTime.of(LocalDateTime.parse("2021-09-08T10:30:00"), ZoneOffset.ofHours(2))
    )
  }

  it should "be defined with '@' and timezone" in {
    evaluateExpression(""" @"2021-09-08T10:30:00@Europe/Berlin" """) should returnResult(
      ZonedDateTime.of(
        LocalDateTime.parse("2021-09-08T10:30:00"),
        ZoneId.of("Europe/Berlin")
      )
    )
  }

  it should "be null if the date is not valid (not a leap year)" in {
    evaluateExpression(""" date and time("2023-02-29T10:00:00") """) should returnNull()
    evaluateExpression(""" @"2023-02-29T10:00:00" """) should returnNull()

    evaluateExpression(""" date and time("2023-02-29T10:00:00+02:00") """) should returnNull()
    evaluateExpression(""" @"2023-02-29T10:00:00+02:00" """) should returnNull()
  }

  it should "be null if the date is not valid (month without 31 days)" in {
    evaluateExpression(""" date and time("2023-06-31T10:00:00") """) should returnNull()
    evaluateExpression(""" @"2023-06-31T10:00:00" """) should returnNull()

    evaluateExpression(""" date and time("2023-06-31T10:00:00+02:00") """) should returnNull()
    evaluateExpression(""" @"2023-06-31T10:00:00+02:00" """) should returnNull()
  }

  "A years-months duration" should "be defined" in {
    evaluateExpression(""" duration("P1Y6M") """) should returnResult(
      Period.ofYears(1).withMonths(6)
    )
  }

  it should "be defined with '@'" in {
    evaluateExpression(""" @"P1Y6M" """) should returnResult(
      Period.ofYears(1).withMonths(6)
    )
  }

  "A days-time duration" should "be defined" in {
    evaluateExpression(""" duration("P1DT12H30M") """) should returnResult(
      Duration.parse("P1DT12H30M")
    )
  }

  it should "be defined with '@'" in {
    evaluateExpression(""" @"P1DT12H30M" """) should returnResult(
      Duration.parse("P1DT12H30M")
    )
  }

}
