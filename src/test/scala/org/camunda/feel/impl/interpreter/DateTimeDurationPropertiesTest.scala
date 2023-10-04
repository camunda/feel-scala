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

import org.camunda.feel._
import org.camunda.feel.impl.FeelIntegrationTest
import org.camunda.feel.syntaxtree._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

/** @author
  *   Philipp Ossler
  */
class DateTimeDurationPropertiesTest extends AnyFlatSpec with Matchers with FeelIntegrationTest {

  "A date" should "has a year property" in {

    eval(""" date("2017-03-10").year """) should be(ValNumber(2017))
  }

  it should "has a month property" in {

    eval(""" date("2017-03-10").month """) should be(ValNumber(3))
  }

  it should "has a day property" in {

    eval(""" date("2017-03-10").day """) should be(ValNumber(10))
  }

  it should "has a weekday property" in {

    eval(""" date("2020-09-30").weekday """) should be(ValNumber(3))
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" date("2020-09-30").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'ValDate(2020-09-30)'. Available properties:"
    )
  }

  it should "has properties with @-notation" in {
    eval(""" @"2017-03-10".year """) should be(ValNumber(2017))
    eval(""" @"2017-03-10".month """) should be(ValNumber(3))
    eval(""" @"2017-03-10".day """) should be(ValNumber(10))
  }

  // /// -----

  "A time" should "has a hour property" in {

    eval(""" time("11:45:30+02:00").hour """) should be(ValNumber(11))
  }

  it should "has a minute property" in {

    eval(""" time("11:45:30+02:00").minute """) should be(ValNumber(45))
  }

  it should "has a second property" in {

    eval(""" time("11:45:30+02:00").second """) should be(ValNumber(30))
  }

  it should "has a time offset property" in {

    eval(""" time("11:45:30+02:00").time offset """) should be(ValDayTimeDuration("PT2H"))
  }

  it should "has a timezone property" in {

    eval(""" time("11:45:30@Europe/Paris").timezone """) should be(ValString("Europe/Paris"))
    eval(""" time("11:45:30+02:00").timezone """) should be(ValNull)
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" time("11:45:30+02:00").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'ValTime(ZonedTime(11:45:30,+02:00,None))'. Available properties:"
    )
  }

  it should "has properties with @-notation" in {
    eval(""" @"11:45:30+02:00".hour """) should be(ValNumber(11))
    eval(""" @"11:45:30+02:00".minute """) should be(ValNumber(45))
    eval(""" @"11:45:30+02:00".second """) should be(ValNumber(30))
  }

  // /// -----

  "A local time" should "has a hour property" in {

    eval(""" time("11:45:30").hour """) should be(ValNumber(11))
  }

  it should "has a minute property" in {

    eval(""" time("11:45:30").minute """) should be(ValNumber(45))
  }

  it should "has a second property" in {

    eval(""" time("11:45:30").second """) should be(ValNumber(30))
  }

  it should "has a time offset property = null" in {

    eval(""" time("11:45:30").time offset """) should be(ValNull)
  }

  it should "has a timezone property = null" in {

    eval(""" time("11:45:30").timezone """) should be(ValNull)
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" time("11:45:30").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'ValLocalTime(11:45:30)'. Available properties:"
    )
  }

  // /// -----

  "A date-time" should "has a year property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").year """) should be(ValNumber(2017))
  }

  it should "has a month property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").month """) should be(ValNumber(3))
  }

  it should "has a day property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").day """) should be(ValNumber(10))
  }

  it should "has a weekday property" in {

    eval(""" date and time("2020-09-30T22:50:30+02:00").weekday """) should be(ValNumber(3))
  }

  it should "has a hour property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").hour """) should be(ValNumber(11))
  }

  it should "has a minute property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").minute """) should be(ValNumber(45))
  }

  it should "has a second property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").second """) should be(ValNumber(30))
  }

  it should "has a time offset property" in {

    eval(""" date and time("2017-03-10T11:45:30+02:00").time offset """) should be(
      ValDayTimeDuration("PT2H")
    )
  }

  it should "has a timezone property" in {

    eval(""" date and time("2017-03-10T11:45:30@Europe/Paris").timezone """) should be(
      ValString("Europe/Paris")
    )
    eval(""" date and time("2017-03-10T11:45:30+02:00").timezone """) should be(ValNull)
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" date and time("2020-09-30T22:50:30+02:00").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'ValDateTime(2020-09-30T22:50:30+02:00)'. Available properties:"
    )
  }

  it should "has properties with @-notation" in {
    eval(""" @"2017-03-10T11:45:30+02:00".year """) should be(ValNumber(2017))
    eval(""" @"2017-03-10T11:45:30+02:00".month """) should be(ValNumber(3))
    eval(""" @"2017-03-10T11:45:30+02:00".day """) should be(ValNumber(10))
  }

  // /// -----

  "A local date-time" should "has a year property" in {

    eval(""" date and time("2017-03-10T11:45:30").year """) should be(ValNumber(2017))
  }

  it should "has a month property" in {

    eval(""" date and time("2017-03-10T11:45:30").month """) should be(ValNumber(3))
  }

  it should "has a day property" in {

    eval(""" date and time("2017-03-10T11:45:30").day """) should be(ValNumber(10))
  }

  it should "has a hour property" in {

    eval(""" date and time("2017-03-10T11:45:30").hour """) should be(ValNumber(11))
  }

  it should "has a minute property" in {

    eval(""" date and time("2017-03-10T11:45:30").minute """) should be(ValNumber(45))
  }

  it should "has a second property" in {

    eval(""" date and time("2017-03-10T11:45:30").second """) should be(ValNumber(30))
  }

  it should "has a time offset property = null" in {

    eval(""" date and time("2017-03-10T11:45:30").time offset """) should be(ValNull)
  }

  it should "has a timezone property = null" in {

    eval(""" date and time("2017-03-10T11:45:30").timezone """) should be(ValNull)
  }

  it should "has a weekday property" in {
    eval(""" date and time("2020-09-30T22:50:30").weekday """) should be(ValNumber(3))
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" date and time("2020-09-30T22:50:30").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'ValLocalDateTime(2020-09-30T22:50:30)'. Available properties:"
    )
  }

  // /// -----

  "A year-month-duration" should "has a years property" in {

    eval(""" duration("P2Y3M").years """) should be(ValNumber(2))
  }

  it should "has a months property" in {

    eval(""" duration("P2Y3M").months """) should be(ValNumber(3))
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" duration("P2Y3M").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'P2Y3M'. Available properties:"
    )
  }

  it should "has properties with @-notation" in {
    eval(""" @"P2Y3M".years """) should be(ValNumber(2))
    eval(""" @"P2Y3M".months """) should be(ValNumber(3))
  }

  // /// -----

  "A day-time-duration" should "has a days property" in {

    eval(""" duration("P1DT2H10M30S").days """) should be(ValNumber(1))
  }

  it should "has a hours property" in {

    eval(""" duration("P1DT2H10M30S").hours """) should be(ValNumber(2))
  }

  it should "has a minutes property" in {

    eval(""" duration("P1DT2H10M30S").minutes """) should be(ValNumber(10))
  }

  it should "has a seconds property" in {

    eval(""" duration("P1DT2H10M30S").seconds """) should be(ValNumber(30))
  }

  it should "return an error if the property is not available" in {
    val result = eval(""" duration("P1DT2H10M30S").x """)

    result shouldBe a[ValError]
    result.asInstanceOf[ValError].error should startWith(
      "No property found with name 'x' of value 'P1DT2H10M30S'. Available properties:"
    )
  }

  it should "has properties with @-notation" in {
    eval(""" @"P1DT2H10M30S".days """) should be(ValNumber(1))
    eval(""" @"P1DT2H10M30S".hours """) should be(ValNumber(2))
    eval(""" @"P1DT2H10M30S".minutes """) should be(ValNumber(10))
    eval(""" @"P1DT2H10M30S".seconds """) should be(ValNumber(30))
  }

}
