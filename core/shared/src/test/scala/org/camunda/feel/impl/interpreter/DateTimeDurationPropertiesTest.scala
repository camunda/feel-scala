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
import org.camunda.feel.impl.{EvaluationResultMatchers, FeelEngineTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Duration, ZonedDateTime}

/** @author
  *   Philipp Ossler
  */
class DateTimeDurationPropertiesTest
    extends AnyFlatSpec
    with Matchers
    with FeelEngineTest
    with EvaluationResultMatchers {

  "A date" should "has a year property" in {

    evaluateExpression(""" date("2017-03-10").year """) should returnResult(2017)
  }

  it should "has a month property" in {

    evaluateExpression(""" date("2017-03-10").month """) should returnResult(3)
  }

  it should "has a day property" in {

    evaluateExpression(""" date("2017-03-10").day """) should returnResult(10)
  }

  it should "has a weekday property" in {

    evaluateExpression(""" date("2020-09-30").weekday """) should returnResult(3)
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" date("2020-09-30").seconds """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'seconds' of value '2020-09-30'. Available properties: 'year', 'month', 'day', 'weekday'"
      )
    )
  }

  it should "has properties with @-notation" in {
    evaluateExpression(""" @"2017-03-10".year """) should returnResult(2017)
    evaluateExpression(""" @"2017-03-10".month """) should returnResult(3)
    evaluateExpression(""" @"2017-03-10".day """) should returnResult(10)
  }

  // /// -----

  "A time" should "has a hour property" in {

    evaluateExpression(""" time("11:45:30+02:00").hour """) should returnResult(11)
  }

  it should "has a minute property" in {

    evaluateExpression(""" time("11:45:30+02:00").minute """) should returnResult(45)
  }

  it should "has a second property" in {

    evaluateExpression(""" time("11:45:30+02:00").second """) should returnResult(30)
  }

  it should "has a time offset property" in {

    evaluateExpression(""" time("11:45:30+02:00").time offset """) should returnResult(
      Duration.parse("PT2H")
    )
  }

  it should "has a timezone property" in {

    evaluateExpression(""" time("11:45:30@Europe/Paris").timezone """) should returnResult(
      "Europe/Paris"
    )

    evaluateExpression(""" time("11:45:30+02:00").timezone """) should returnNull()
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" time("11:45:30+02:00").day """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'day' of value '11:45:30+02:00'. Available properties: 'timezone', 'second', 'time offset', 'minute', 'hour'"
      )
    )
  }

  it should "has properties with @-notation" in {
    evaluateExpression(""" @"11:45:30+02:00".hour """) should returnResult(11)
    evaluateExpression(""" @"11:45:30+02:00".minute """) should returnResult(45)
    evaluateExpression(""" @"11:45:30+02:00".second """) should returnResult(30)
  }

  // /// -----

  "A local time" should "has a hour property" in {

    evaluateExpression(""" time("11:45:30").hour """) should returnResult(11)
  }

  it should "has a minute property" in {

    evaluateExpression(""" time("11:45:30").minute """) should returnResult(45)
  }

  it should "has a second property" in {

    evaluateExpression(""" time("11:45:30").second """) should returnResult(30)
  }

  it should "has a time offset property = null" in {

    evaluateExpression(""" time("11:45:30").time offset """) should returnNull()
  }

  it should "has a timezone property = null" in {

    evaluateExpression(""" time("11:45:30").timezone """) should returnNull()
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" time("11:45:30").day """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'day' of value '11:45:30'. Available properties: 'timezone', 'second', 'time offset', 'minute', 'hour'"
      )
    )
  }

  // /// -----

  "A date-time" should "has a year property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30+02:00").year """) should returnResult(
      2017
    )
  }

  it should "has a month property" in {

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30+02:00").month """
    ) should returnResult(3)
  }

  it should "has a day property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30+02:00").day """) should returnResult(
      10
    )
  }

  it should "has a weekday property" in {

    evaluateExpression(
      """ date and time("2020-09-30T22:50:30+02:00").weekday """
    ) should returnResult(3)
  }

  it should "has a hour property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30+02:00").hour """) should returnResult(
      11
    )
  }

  it should "has a minute property" in {

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30+02:00").minute """
    ) should returnResult(45)
  }

  it should "has a second property" in {

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30+02:00").second """
    ) should returnResult(30)
  }

  it should "has a time offset property" in {

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30+02:00").time offset """
    ) should returnResult(
      Duration.parse("PT2H")
    )
  }

  it should "has a variable with a time offset property" in {

    evaluateExpression(
      expression = """ dateTime.time offset """,
      variables = Map("dateTime" -> ZonedDateTime.parse("2017-03-10T11:45:30+02:00"))
    ) should returnResult(Duration.parse("PT2H"))
  }

  it should "has a timezone property" in {

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30@Europe/Paris").timezone """
    ) should returnResult("Europe/Paris")

    evaluateExpression(
      """ date and time("2017-03-10T11:45:30+02:00").timezone """
    ) should returnNull()
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" date and time("2020-09-30T22:50:30+02:00").days """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'days' of value '2020-09-30T22:50:30+02:00'. Available properties: 'timezone', 'year', 'second', 'month', 'day', 'time offset', 'weekday', 'minute', 'hour'"
      )
    )
  }

  it should "has properties with @-notation" in {
    evaluateExpression(""" @"2017-03-10T11:45:30+02:00".year """) should returnResult(2017)
    evaluateExpression(""" @"2017-03-10T11:45:30+02:00".month """) should returnResult(3)
    evaluateExpression(""" @"2017-03-10T11:45:30+02:00".day """) should returnResult(10)
  }

  // /// -----

  "A local date-time" should "has a year property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").year """) should returnResult(2017)
  }

  it should "has a month property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").month """) should returnResult(3)
  }

  it should "has a day property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").day """) should returnResult(10)
  }

  it should "has a hour property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").hour """) should returnResult(11)
  }

  it should "has a minute property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").minute """) should returnResult(45)
  }

  it should "has a second property" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").second """) should returnResult(30)
  }

  it should "has a time offset property = null" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").time offset """) should returnNull()
  }

  it should "has a timezone property = null" in {

    evaluateExpression(""" date and time("2017-03-10T11:45:30").timezone """) should returnNull()
  }

  it should "has a weekday property" in {
    evaluateExpression(""" date and time("2020-09-30T22:50:30").weekday """) should returnResult(3)
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" date and time("2020-09-30T22:50:30").days """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'days' of value '2020-09-30T22:50:30'. Available properties: 'timezone', 'year', 'second', 'month', 'day', 'time offset', 'weekday', 'minute', 'hour'"
      )
    )
  }

  // /// -----

  "A year-month-duration" should "has a years property" in {

    evaluateExpression(""" duration("P2Y3M").years """) should returnResult(2)
  }

  it should "has a months property" in {

    evaluateExpression(""" duration("P2Y3M").months """) should returnResult(3)
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" duration("P2Y3M").day """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'day' of value 'P2Y3M'. Available properties: 'years', 'months'"
      )
    )
  }

  it should "has properties with @-notation" in {
    evaluateExpression(""" @"P2Y3M".years """) should returnResult(2)
    evaluateExpression(""" @"P2Y3M".months """) should returnResult(3)
  }

  // /// -----

  "A day-time-duration" should "has a days property" in {

    evaluateExpression(""" duration("P1DT2H10M30S").days """) should returnResult(1)
  }

  it should "has a hours property" in {

    evaluateExpression(""" duration("P1DT2H10M30S").hours """) should returnResult(2)
  }

  it should "has a minutes property" in {

    evaluateExpression(""" duration("P1DT2H10M30S").minutes """) should returnResult(10)
  }

  it should "has a seconds property" in {

    evaluateExpression(""" duration("P1DT2H10M30S").seconds """) should returnResult(30)
  }

  it should "return null if the property is not available" in {
    evaluateExpression(""" duration("P1DT2H10M30S").day """) should (
      returnNull() and reportFailure(
        failureType = EvaluationFailureType.NO_PROPERTY_FOUND,
        failureMessage =
          "No property found with name 'day' of value 'P1DT2H10M30S'. Available properties: 'days', 'hours', 'minutes', 'seconds'"
      )
    )
  }

  it should "has properties with @-notation" in {
    evaluateExpression(""" @"P1DT2H10M30S".days """) should returnResult(1)
    evaluateExpression(""" @"P1DT2H10M30S".hours """) should returnResult(2)
    evaluateExpression(""" @"P1DT2H10M30S".minutes """) should returnResult(10)
    evaluateExpression(""" @"P1DT2H10M30S".seconds """) should returnResult(30)
  }

}
