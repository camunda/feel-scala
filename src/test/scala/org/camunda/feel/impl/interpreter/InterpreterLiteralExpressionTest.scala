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

import java.time.{
  Duration,
  LocalDate,
  LocalDateTime,
  LocalTime,
  Period,
  ZoneId,
  ZoneOffset,
  ZonedDateTime
}

/** @author
  *   Philipp Ossler
  */
class InterpreterLiteralExpressionTest extends AnyFlatSpec with Matchers with FeelIntegrationTest {

  "A literal" should "be a number" in {

    eval("2") should be(ValNumber(2))
    eval("2.4") should be(ValNumber(2.4))
    eval("-3") should be(ValNumber(-3))
    eval("02") should be(ValNumber(2))
    eval("02.4") should be(ValNumber(2.4))
    eval("-03") should be(ValNumber(-3))
    eval("0000002") should be(ValNumber(2))

  }

  it should "be a string" in {

    eval(""" "a" """) should be(ValString("a"))
  }

  it should "be a boolean" in {

    eval("true") should be(ValBoolean(true))
  }

  it should "be null" in {

    eval("null") should be(ValNull)
  }

  it should "be a context (identifier as key)" in {

    eval("{ a : 1 }")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(1)))

    eval("""{ a:1, b:"foo" }""")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("a" -> ValNumber(1), "b" -> ValString("foo")))

    // nested
    val nestedContext = eval("{ a : { b : 1 } }")
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariable("a")
      .get

    nestedContext shouldBe a[ValContext]
    nestedContext
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables should be(Map("b" -> ValNumber(1)))
  }

  it should "be a context (string as key)" in {
    val result = eval(""" {"a":1} """)

    result shouldBe a[ValContext]
    result match {
      case ValContext(context) =>
        context.variableProvider.getVariables should be(Map("a" -> ValNumber(1)))
    }
  }

  it should "be a list" in {

    eval("[1]") should be(ValList(List(ValNumber(1))))

    eval("[1,2]") should be(ValList(List(ValNumber(1), ValNumber(2))))

    // nested
    eval("[ [1], [2] ]") should be(
      ValList(List(ValList(List(ValNumber(1))), ValList(List(ValNumber(2)))))
    )
  }

  "A date literal" should "be defined" in {
    eval(""" date("2021-09-08") """) should be(
      ValDate(
        LocalDate.parse("2021-09-08")
      )
    )
  }

  it should "be defined with '@'" in {
    eval(""" @"2021-09-08" """) should be(
      ValDate(
        LocalDate.parse("2021-09-08")
      )
    )
  }

  it should "be null if the date is not valid (leap year)" in {
    eval(""" date("2023-02-29") """) should be(ValNull)
    eval(""" @"2023-02-29" """) should be(ValNull)
  }

  it should "be null if the date is not valid (month with 31 days)" in {
    eval(""" date("2023-06-31") """) should be(ValNull)
    eval(""" @"2023-06-31" """) should be(ValNull)
  }

  "A time literal" should "be defined without offset" in {
    eval(""" time("10:30:00") """) should be(
      ValLocalTime(
        LocalTime.parse("10:30:00")
      )
    )
  }

  it should "be defined with offset" in {
    eval(""" time("10:30:00+02:00") """) should be(
      ValTime(
        ZonedTime.of(time = LocalTime.parse("10:30:00"), offset = ZoneOffset.ofHours(2))
      )
    )
  }

  it should "be defined with timezone" in {
    eval(""" time("10:30:00@Europe/Berlin") """) should be(
      ValTime(
        ZonedTime.of(
          time = LocalTime.parse("10:30:00"),
          zoneId = ZoneId.of("Europe/Berlin")
        )
      )
    )
  }

  it should "be defined with '@' and no offset" in {
    eval(""" @"10:30:00" """) should be(
      ValLocalTime(
        LocalTime.parse("10:30:00")
      )
    )
  }

  it should "be defined with '@' and offset" in {
    eval(""" @"10:30:00+02:00" """) should be(
      ValTime(
        ZonedTime.of(time = LocalTime.parse("10:30:00"), offset = ZoneOffset.ofHours(2))
      )
    )
  }

  it should "be defined with '@' and timezone" in {
    eval(""" @"10:30:00@Europe/Berlin" """) should be(
      ValTime(
        ZonedTime.of(
          time = LocalTime.parse("10:30:00"),
          zoneId = ZoneId.of("Europe/Berlin")
        )
      )
    )
  }

  "A date-time literal" should "be defined without offset" in {
    eval(""" date and time("2021-09-08T10:30:00") """) should be(
      ValLocalDateTime(
        LocalDateTime.parse("2021-09-08T10:30:00")
      )
    )
  }

  it should "be defined with offset" in {
    eval(""" date and time("2021-09-08T10:30:00+02:00") """) should be(
      ValDateTime(
        ZonedDateTime.of(LocalDateTime.parse("2021-09-08T10:30:00"), ZoneOffset.ofHours(2))
      )
    )
  }

  it should "be defined with timezone" in {
    eval(""" date and time("2021-09-08T10:30:00@Europe/Berlin") """) should be(
      ValDateTime(
        ZonedDateTime.of(
          LocalDateTime.parse("2021-09-08T10:30:00"),
          ZoneId.of("Europe/Berlin")
        )
      )
    )
  }

  it should "be defined with '@' and no offset" in {
    eval(""" @"2021-09-08T10:30:00" """) should be(
      ValLocalDateTime(
        LocalDateTime.parse("2021-09-08T10:30:00")
      )
    )
  }

  it should "be defined with '@' and offset" in {
    eval(""" @"2021-09-08T10:30:00+02:00" """) should be(
      ValDateTime(
        ZonedDateTime.of(LocalDateTime.parse("2021-09-08T10:30:00"), ZoneOffset.ofHours(2))
      )
    )
  }

  it should "be defined with '@' and timezone" in {
    eval(""" @"2021-09-08T10:30:00@Europe/Berlin" """) should be(
      ValDateTime(
        ZonedDateTime.of(
          LocalDateTime.parse("2021-09-08T10:30:00"),
          ZoneId.of("Europe/Berlin")
        )
      )
    )
  }

  it should "be null if the date is not valid (leap year)" in {
    eval(""" date and time("2023-02-29T10:00:00") """) should be(ValNull)
    eval(""" @"2023-02-29T10:00:00" """) should be(ValNull)

    eval(""" date and time("2023-02-29T10:00:00+02:00") """) should be(ValNull)
    eval(""" @"2023-02-29T10:00:00+02:00" """) should be(ValNull)
  }

  it should "be null if the date is not valid (month with 31 days)" in {
    eval(""" date and time("2023-06-31T10:00:00") """) should be(ValNull)
    eval(""" @"2023-06-31T10:00:00" """) should be(ValNull)

    eval(""" date and time("2023-06-31T10:00:00+02:00") """) should be(ValNull)
    eval(""" @"2023-06-31T10:00:00+02:00" """) should be(ValNull)
  }

  "A years-months duration" should "be defined" in {
    eval(""" duration("P1Y6M") """) should be(
      ValYearMonthDuration(
        Period.ofYears(1).withMonths(6)
      )
    )
  }

  it should "be defined with '@'" in {
    eval(""" @"P1Y6M" """) should be(
      ValYearMonthDuration(
        Period.ofYears(1).withMonths(6)
      )
    )
  }

  "A days-time duration" should "be defined" in {
    eval(""" duration("P1DT12H30M") """) should be(
      ValDayTimeDuration(
        Duration.parse("P1DT12H30M")
      )
    )
  }

  it should "be defined with '@'" in {
    eval(""" @"P1DT12H30M" """) should be(
      ValDayTimeDuration(
        Duration.parse("P1DT12H30M")
      )
    )
  }

}
