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

import org.camunda.feel.api.EvaluationFailureType.FUNCTION_INVOCATION_FAILURE
import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.impl.interpreter.MyCustomContext
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.CustomValueMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time._
import scala.math.BigDecimal.int2bigDecimal

/** @author
  *   Philipp
  */
class BuiltinConversionFunctionsTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A date() function" should "convert String" in {

    evaluateExpression(""" date(x) """, Map("x" -> "2012-12-25")) should returnResult(
      LocalDate.parse("2012-12-25")
    )
  }

  it should "convert Date-Time" in {

    evaluateExpression(""" date( date and time("2012-12-25T11:00:00") ) """) should returnResult(
      LocalDate.parse("2012-12-25")
    )

    evaluateExpression(
      """ date( date and time("2012-12-25T11:00:00+01:00") ) """
    ) should returnResult(
      LocalDate.parse("2012-12-25")
    )
  }

  it should "convert (year,month,day)" in {

    evaluateExpression(""" date(2012, 12, 25) """) should returnResult(
      LocalDate.parse("2012-12-25")
    )
  }

  it should "return null if the date is not valid (not a leap year)" in {

    evaluateExpression(""" date(x) """, Map("x" -> "2023-02-29")) should returnNull()
    evaluateExpression(""" date(2023, 2, 29) """) should returnNull()
  }

  it should "return null if the date is not valid (month without 31 days)" in {

    evaluateExpression(""" date(x) """, Map("x" -> "2023-06-31")) should returnNull()
    evaluateExpression(""" date(2023, 6, 31) """) should returnNull()
  }

  "A date and time() function" should "convert String" in {

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2012-12-24T23:59:00")
    ) should returnResult(
      LocalDateTime.parse("2012-12-24T23:59:00")
    )

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2012-12-24T23:59:00+01:00")
    ) should returnResult(
      ZonedDateTime.parse("2012-12-24T23:59:00+01:00")
    )
  }

  it should "convert (DateTime, Timezone)" in {
    evaluateExpression(
      """date and time(@"2020-07-31T14:27:30@Europe/Berlin", "Z")"""
    ) should returnResult(
      ZonedDateTime.parse("2020-07-31T12:27:30Z")
    )

    evaluateExpression(
      """date and time(@"2020-07-31T14:27:30@Europe/Berlin", "America/Los_Angeles")"""
    ) should returnResult(ZonedDateTime.parse("2020-07-31T05:27:30-07:00[America/Los_Angeles]"))

    evaluateExpression("""date and time(@"2020-07-31T14:27:30", "Z")""") should returnResult(
      ZonedDateTime.parse("2020-07-31T14:27:30Z")
    )
  }

  it should "convert (Date,Time)" in {

    evaluateExpression(
      """ date and time(date("2012-12-24"),time("T23:59:00")) """
    ) should returnResult(
      LocalDateTime.parse("2012-12-24T23:59:00")
    )

    evaluateExpression(
      """ date and time(date("2012-12-24"),time("T23:59:00+01:00")) """
    ) should returnResult(
      ZonedDateTime.parse("2012-12-24T23:59:00+01:00")
    )
  }

  it should "convert (DateTime,Time)" in {

    evaluateExpression(
      """ date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00")) """
    ) should returnResult(
      LocalDateTime.parse("2012-12-24T23:59:00")
    )
    evaluateExpression(
      """ date and time(date and time("2012-12-24T10:24:00"),time("T23:59:00+01:00")) """
    ) should returnResult(ZonedDateTime.parse("2012-12-24T23:59:00+01:00"))
    evaluateExpression(
      """ date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00")) """
    ) should returnResult(LocalDateTime.parse("2012-12-24T23:59:00"))
    evaluateExpression(
      """ date and time(date and time("2012-12-24T10:24:00+01:00"),time("T23:59:00+01:00")) """
    ) should returnResult(ZonedDateTime.parse("2012-12-24T23:59:00+01:00"))
  }

  it should "return null if the date is not valid (not a leap year)" in {

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2023-02-29T10:00:00")
    ) should returnNull()

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2023-02-29T10:00:00+02:00")
    ) should returnNull()
  }

  it should "return null if the date is not valid (month without 31 days)" in {

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2023-06-31T10:00:00")
    ) should returnNull()

    evaluateExpression(
      """ date and time(x) """,
      Map("x" -> "2023-06-31T10:00:00+02:00")
    ) should returnNull()
  }

  "A time() function" should "convert String" in {

    evaluateExpression(""" time(x) """, Map("x" -> "23:59:00")) should returnResult(
      LocalTime.parse("23:59:00")
    )

    evaluateExpression(""" time(x) """, Map("x" -> "23:59:00+01:00")) should returnResult(
      OffsetTime.parse("23:59:00+01:00")
    )

    evaluateExpression(""" time(x) """, Map("x" -> "23:59:00@Europe/Paris")) should returnResult(
      OffsetTime.parse("23:59:00+01:00")
    )
  }

  it should "convert Date-Time" in {

    evaluateExpression(""" time( date and time("2012-12-25T11:00:00") ) """) should returnResult(
      LocalTime.parse("11:00:00")
    )

    evaluateExpression(
      """ time( date and time("2012-12-25T11:00:00+01:00") ) """
    ) should returnResult(
      OffsetTime.parse("11:00:00+01:00")
    )
  }

  it should "convert (hour,minute,second)" in {

    evaluateExpression(""" time(23, 59, 0) """) should returnResult(LocalTime.parse("23:59:00"))
  }

  it should "convert (hour,minute,second, offset)" in {

    evaluateExpression(""" time(14, 30, 0, duration("PT1H")) """) should returnResult(
      OffsetTime.parse("14:30:00+01:00")
    )
  }

  "A number() function" should "convert String" in {

    evaluateExpression(""" number("1500.5") """) should returnResult(1500.5)
  }

  it should "convert String with Grouping Separator ' '" in {

    evaluateExpression(""" number("1 500.5", " ") """) should returnResult(1500.5)
  }

  it should "convert String with Grouping Separator ','" in {

    evaluateExpression(""" number("1,500", ",") """) should returnResult(1500)
  }

  it should "convert String with Grouping Separator '.'" in {

    evaluateExpression(""" number("1.500", ".") """) should returnResult(1500)
  }

  it should "convert String with Grouping ' ' and Decimal Separator '.'" in {

    evaluateExpression(""" number("1 500.5", " ", ".") """) should returnResult(1500.5)
  }

  it should "convert String with Grouping ' ' and Decimal Separator ','" in {

    evaluateExpression(""" number("1 500,5", " ", ",") """) should returnResult(1500.5)
  }

  it should "convert String with Grouping null and Decimal Separator ','" in {

    evaluateExpression(""" number("1500,5", null, ",") """) should returnResult(1500.5)
  }

  it should "convert String with Grouping '.' and Decimal null" in {

    evaluateExpression(""" number("1.500", ".", null) """) should returnResult(1500)
  }

  it should "be invoked with named parameter" in {

    evaluateExpression(
      """ number(from: "1.500", grouping separator: ".", decimal separator: null) """
    ) should returnResult(
      1500
    )
  }

  it should "return null if the string is not a number" in {

    evaluateExpression(""" number("x") """) should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'number': Can't parse 'x' as a number"
      )
    )

    evaluateExpression(""" number("x", ".") """) should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'number': Can't parse 'x' as a number"
      )
    )

    evaluateExpression(""" number("x", ".", ",") """) should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'number': Can't parse 'x' as a number"
      )
    )
  }

  "A string() function" should "convert Number" in {

    evaluateExpression(""" string(1.1) """) should returnResult("1.1")
  }

  it should "convert a string" in {
    evaluateExpression(""" string("hello") """) should returnResult("hello")
  }

  it should "convert Boolean" in {

    evaluateExpression(""" string(true) """) should returnResult("true")
  }

  it should "convert Date" in {

    evaluateExpression(""" string(date("2012-12-25")) """) should returnResult("2012-12-25")
  }

  it should "convert Time" in {

    evaluateExpression(""" string(time("23:59:00")) """) should returnResult("23:59:00")
    evaluateExpression(""" string(time("23:59:00+01:00")) """) should returnResult("23:59:00+01:00")
  }

  it should "convert Date-Time" in {

    evaluateExpression(""" string(date and time("2012-12-25T11:00:00")) """) should returnResult(
      "2012-12-25T11:00:00"
    )
    evaluateExpression(
      """ string(date and time("2012-12-25T11:00:00+02:00")) """
    ) should returnResult(
      "2012-12-25T11:00:00+02:00"
    )
  }

  it should "convert zero-length days-time-duration" in {
    evaluateExpression(""" string(@"-PT0S") """) should returnResult("P0D")
    evaluateExpression(""" string(@"P0D") """) should returnResult("P0D")
    evaluateExpression(""" string(@"PT0H") """) should returnResult("P0D")
    evaluateExpression(""" string(@"PT0H0M") """) should returnResult("P0D")
    evaluateExpression(""" string(@"PT0H0M0S") """) should returnResult("P0D")
    evaluateExpression(""" string(@"P0DT0H0M0S") """) should returnResult("P0D")
  }

  it should "convert negative days-time-duration" in {

    evaluateExpression(""" string(@"-PT1S") """) should returnResult("-PT1S")
    evaluateExpression(""" string(@"-PT1H") """) should returnResult("-PT1H")
    evaluateExpression(""" string(@"-PT2M30S") """) should returnResult("-PT2M30S")
    evaluateExpression(""" string(@"-P1DT2H3M4S") """) should returnResult("-P1DT2H3M4S")
  }

  it should "convert days-time-duration" in {

    evaluateExpression(""" string(@"PT1H") """) should returnResult("PT1H")
    evaluateExpression(""" string(@"PT2M30S") """) should returnResult("PT2M30S")
    evaluateExpression(""" string(@"P1DT2H3M4S") """) should returnResult("P1DT2H3M4S")
  }

  it should "convert zero-length years-months-duration" in {

    evaluateExpression(""" string(@"P0Y") """) should returnResult("P0Y")
    evaluateExpression(""" string(@"-P0M") """) should returnResult("P0Y")
    evaluateExpression(""" string(@"P0Y0M") """) should returnResult("P0Y")
  }
  it should "convert negative years-months-duration" in {

    evaluateExpression(""" string(@"-P1Y") """) should returnResult("-P1Y")
    evaluateExpression(""" string(@"-P5M") """) should returnResult("-P5M")
    evaluateExpression(""" string(@"-P3Y1M") """) should returnResult("-P3Y1M")
  }
  it should "convert years-months-duration" in {

    evaluateExpression(""" string(@"P1Y") """) should returnResult("P1Y")
    evaluateExpression(""" string(@"P2M") """) should returnResult("P2M")
    evaluateExpression(""" string(@"P1Y2M") """) should returnResult("P1Y2M")
  }

  it should "return null if the argument is null" in {
    evaluateExpression(""" string(null) """) should returnNull()
  }

  it should "convert a list" in {
    evaluateExpression(" string([]) ") should returnResult("[]")
    evaluateExpression(" string([1,2,3]) ") should returnResult("[1, 2, 3]")
  }

  it should "convert a context" in {
    evaluateExpression(" string({}) ") should returnResult("{}")
    evaluateExpression(" string({a:1,b:2}) ") should returnResult("{a:1, b:2}")
  }

  case class CustomValue(value: Int)

  class MyCustomValueMapper extends CustomValueMapper {
    def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = x match {
      case CustomValue(value) => Some(ValNumber(value))
      case _                  => None
    }

    override def unpackVal(value: Val, innerValueMapper: Val => Any): Option[Any] = None
  }

  it should "convert a custom context" in {

    val engine = FeelEngineBuilder()
      .withCustomValueMapper(new MyCustomValueMapper())
      .build()

    engine.evaluateExpression(
      expression = " string(context) ",
      variables = Map("context" -> Map("a" -> CustomValue(1)))
    ) should returnResult("{a:1}")

    engine.evaluateExpression(
      expression = " string(context) ",
      variables = Map("context" -> ValContext(new MyCustomContext(Map("a" -> CustomValue(1)))))
    ) should returnResult(
      "{a:1}"
    )
  }

  it should "convert a list containing a custom context" in {

    val engine = FeelEngineBuilder()
      .withCustomValueMapper(new MyCustomValueMapper())
      .build()

    engine.evaluateExpression(
      expression = " string(list) ",
      variables = Map("list" -> List(Map("a" -> CustomValue(1)), CustomValue(2)))
    ) should returnResult("[{a:1}, 2]")

    engine.evaluateExpression(
      expression = " string(list) ",
      variables =
        Map("list" -> List(Map("l" -> List(CustomValue(1), CustomValue(2))), CustomValue(3)))
    ) should returnResult("[{l:[1, 2]}, 3]")

    engine.evaluateExpression(
      expression = " string(list) ",
      variables = Map(
        "list" -> List(ValContext(new MyCustomContext(Map("a" -> CustomValue(1)))), CustomValue(2))
      )
    ) should returnResult("[{a:1}, 2]")

    engine.evaluateExpression(
      expression = " string(list) ",
      variables = Map(
        "list" -> List(
          ValContext(new MyCustomContext(Map("l" -> List(CustomValue(1), CustomValue(2))))),
          CustomValue(3)
        )
      )
    ) should returnResult("[{l:[1, 2]}, 3]")
  }

  it should "convert a nested custom context" in {

    val engine = FeelEngineBuilder()
      .withCustomValueMapper(new MyCustomValueMapper())
      .build()

    engine.evaluateExpression(
      expression = " string(context) ",
      variables = Map(
        "context" ->
          ValContext(
            new MyCustomContext(
              Map("ctx" -> ValContext(new MyCustomContext(Map("a" -> CustomValue(1)))))
            )
          )
      )
    ) should returnResult("{ctx:{a:1}}")
  }

  "A duration() function" should "convert day-time-String" in {

    evaluateExpression(""" duration(x) """, Map("x" -> "P2DT20H14M")) should returnResult(
      Duration.parse("P2DT20H14M")
    )
  }

  it should "convert day-time-String with negative duration" in {
    evaluateExpression(""" duration(x) """, Map("x" -> "-PT5M")) should returnResult(
      Duration.parse("-PT5M")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "PT-5M")) should returnResult(
      Duration.parse("PT-5M")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "P-1D")) should returnResult(
      Duration.parse("P-1D")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "PT-2H")) should returnResult(
      Duration.parse("PT-2H")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "PT-3M-4S")) should returnResult(
      Duration.parse("PT-3M-4S")
    )
  }

  it should "convert year-month-String" in {

    evaluateExpression(""" duration(x) """, Map("x" -> "P2Y4M")) should returnResult(
      Period.parse("P2Y4M")
    )
  }

  it should "convert year-month-String with negative duration" in {
    evaluateExpression(""" duration(x) """, Map("x" -> "-P1Y2M")) should returnResult(
      Period.parse("-P1Y2M")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "P-1Y")) should returnResult(
      Period.parse("P-1Y")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "P-2M")) should returnResult(
      Period.parse("P-2M")
    )

    evaluateExpression(""" duration(x) """, Map("x" -> "P-1Y-2M")) should returnResult(
      Period.parse("P-1Y-2M")
    )
  }

  "A years and months duration(from,to) function" should "convert (Date,Date)" in {

    evaluateExpression(
      """ years and months duration( date("2011-12-22"), date("2013-08-24") ) """
    ) should returnResult(
      Period.parse("P1Y8M")
    )
    evaluateExpression(
      """ years and months duration( date and time("2011-12-22T10:00:00"), date and time("2013-08-24T10:00:00") ) """
    ) should returnResult(Period.parse("P1Y8M"))
    evaluateExpression(
      """ years and months duration( date and time("2011-12-22T10:00:00+01:00"), date and time("2013-08-24T10:00:00+01:00") ) """
    ) should returnResult(Period.parse("P1Y8M"))
  }

  "A from json() function" should "convert a JSON object to a context" in {
    evaluateExpression(
      """ from json(value) """,
      Map("value" -> "{\"a\": 1, \"b\": 2}")
    ) should returnResult(
      Map("a" -> 1, "b" -> 2)
    )
  }

  it should "convert a JSON array to a list" in {
    evaluateExpression(""" from json(value) """, Map("value" -> "[1, 2, 3]")) should returnResult(
      List(1, 2, 3)
    )
  }

  it should "convert a JSON null value" in {
    evaluateExpression(""" from json("null") """) should returnResult(
      null
    )
  }

  it should "convert a JSON number" in {
    evaluateExpression(""" from json(value) """, Map("value" -> "1")) should returnResult(
      1
    )
  }

  it should "convert a JSON string" in {
    evaluateExpression(""" from json(value) """, Map("value" -> "\"a\"")) should returnResult(
      "a"
    )

    evaluateExpression(
      """ from json(value) """,
      Map("value" -> "\"2023-06-14\"")
    ) should returnResult(
      "2023-06-14"
    )

    evaluateExpression(
      """ from json(value) """,
      Map("value" -> "\"14:55:00\"")
    ) should returnResult(
      "14:55:00"
    )

    evaluateExpression(
      """ from json(value) """,
      Map("value" -> "\"2023-06-14T14:55:00\"")
    ) should returnResult(
      "2023-06-14T14:55:00"
    )

    evaluateExpression(""" from json(value) """, Map("value" -> "\"P1Y\"")) should returnResult(
      "P1Y"
    )

    evaluateExpression(""" from json(value) """, Map("value" -> "\"PT2H\"")) should returnResult(
      "PT2H"
    )
  }

  it should "convert a JSON boolean" in {
    evaluateExpression(""" from json(value) """, Map("value" -> "true")) should returnResult(
      true
    )

    evaluateExpression(""" from json(value) """, Map("value" -> "false")) should returnResult(
      false
    )
  }

  it should "return null if the JSON is invalid" in {
    evaluateExpression(""" from json(value) """, Map("value" -> "invalid")) should (
      returnNull() and reportFailure(
        FUNCTION_INVOCATION_FAILURE,
        "Failed to invoke function 'from json': Failed to parse JSON from 'invalid'"
      )
    )
  }
}
