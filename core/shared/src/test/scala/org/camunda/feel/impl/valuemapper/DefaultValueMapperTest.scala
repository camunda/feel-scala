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
package org.camunda.feel.impl.valuemapper

import java.time._
import org.camunda.feel._
import org.camunda.feel.context.Context
import org.camunda.feel.impl._
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import java.text.SimpleDateFormat
import java.util

/** @author
  *   Philipp Ossler
  * @author
  *   Falko Menge
  */
class DefaultValueMapperTest extends AnyFlatSpec with Matchers {

  implicit val valueMapper: ValueMapper =
    ValueMapper.CompositeValueMapper(List(DefaultValueMapper.instance))

  "The DefaultValueMapper" should "convert from String" in {

    valueMapper.toVal("foo") should be(ValString("foo"))
  }

  it should "convert from java.lang.String" in {

    valueMapper.toVal(new java.lang.String("foo")) should be(ValString("foo"))
  }

  it should "convert from Int" in {

    valueMapper.toVal(4: Int) should be(ValNumber(4))
  }

  it should "convert from java.lang.Integer" in {

    valueMapper.toVal(new java.lang.Integer(4)) should be(ValNumber(4))
  }

  it should "convert from Long" in {

    valueMapper.toVal(4: Long) should be(ValNumber(4))
  }

  it should "convert from java.lang.Long" in {

    valueMapper.toVal(new java.lang.Long(4)) should be(ValNumber(4))
  }

  it should "convert from Float" in {

    valueMapper.toVal(2.4f: Float) should be(ValNumber(2.4f))
  }

  it should "convert from java.lang.Float" in {

    valueMapper.toVal(java.lang.Float.valueOf(2.4f)) should be(ValNumber(2.4f))
  }

  it should "convert from Double" in {

    valueMapper.toVal(2.4: Double) should be(ValNumber(2.4))
  }

  it should "convert from java.lang.Double" in {

    valueMapper.toVal(new java.lang.Double(2.4)) should be(ValNumber(2.4))
  }

  it should "convert from BigDecimal" in {

    valueMapper.toVal(2.4: BigDecimal) should be(ValNumber(2.4))
  }

  it should "convert from java.math.BigDecimal" in {

    valueMapper.toVal(new java.math.BigDecimal("2.4")) should be(ValNumber(2.4))
  }

  it should "convert from Boolean" in {

    valueMapper.toVal(true) should be(ValBoolean(true))
  }

  it should "convert from java.lang.Boolean" in {

    valueMapper.toVal(java.lang.Boolean.TRUE) should be(ValBoolean(true))
  }

  it should "convert from List" in {

    valueMapper.toVal(List(1, 2)) should be(ValList(Seq(ValNumber(1), ValNumber(2))))
  }

  it should "convert from java.util.List" in {

    valueMapper.toVal(java.util.Arrays.asList(1, 2)) should be(
      ValList(Seq(ValNumber(1), ValNumber(2)))
    )
  }

  it should "convert from Map" in {
    valueMapper.toVal(Map("a" -> 2)) should be(
      ValContext(Context.StaticContext(Map("a" -> ValNumber(2))))
    )
  }

  it should "convert from java.util.Map" in {

    val map = new java.util.HashMap[String, Object]
    map.put("a", new java.lang.Integer(2))

    valueMapper.toVal(map) should be(ValContext(Context.StaticContext(Map("a" -> ValNumber(2)))))
  }

  it should "convert from LocalDate" in {

    valueMapper.toVal(java.time.LocalDate.parse("2017-04-02")) should be(ValDate("2017-04-02"))
  }

  it should "convert from LocalTime" in {

    valueMapper.toVal(java.time.LocalTime.parse("12:04:30")) should be(ValLocalTime("12:04:30"))
  }

  it should "convert from OffsetTime" in {

    valueMapper.toVal(java.time.OffsetTime.parse("12:04:30+01:00")) should be(
      ValTime("12:04:30+01:00")
    )
  }

  it should "convert from LocalDateTime" in {

    valueMapper.toVal(java.time.LocalDateTime.parse("2017-04-02T12:04:30")) should be(
      ValLocalDateTime("2017-04-02T12:04:30")
    )
  }

  it should "convert from OffsetDateTime" in {

    valueMapper.toVal(java.time.OffsetDateTime.parse("2017-04-02T12:04:30+01:00")) should be(
      ValDateTime("2017-04-02T12:04:30+01:00")
    )
  }

  it should "convert from ZonedDateTime with zone offset" in {

    valueMapper.toVal(java.time.ZonedDateTime.parse("2017-04-02T12:04:30+01:00")) should be(
      ValDateTime("2017-04-02T12:04:30+01:00")
    )
  }

  it should "convert from ZonedDateTime with zone offset 'Z'" in {

    valueMapper.toVal(java.time.ZonedDateTime.parse("2017-04-02T12:04:30Z")) should be(
      ValDateTime("2017-04-02T12:04:30Z")
    )
  }

  it should "convert from ZonedDateTime with zone id" in {

    valueMapper.toVal(
      java.time.ZonedDateTime
        .parse("2017-04-02T12:04:30+02:00[Europe/Paris]")
    ) should be(ValDateTime("2017-04-02T12:04:30@Europe/Paris"))
  }

  it should "convert from java.util.Date" in {

    val dateTime = LocalDateTime.of(2017, 4, 2, 12, 4, 30).atZone(ZoneId.of("UTC"))

    val date                  = new util.Date(dateTime.toInstant.toEpochMilli)
    // java.util.Date is converted using the system's default timezone
    val expectedLocalDateTime = dateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime
    valueMapper.toVal(date) should be(ValLocalDateTime(expectedLocalDateTime))
  }

  it should "convert from Period" in {

    valueMapper.toVal(java.time.Period.parse("P2Y4M")) should be(ValYearMonthDuration("P2Y4M"))
  }

  it should "convert from Duration" in {

    valueMapper.toVal(java.time.Duration.parse("PT4H22M")) should be(ValDayTimeDuration("PT4H22M"))
  }

  it should "convert from object" in {
    case class Obj(val a: Int, val b: String)

    val result = valueMapper.toVal(Obj(a = 2, b = "foo"))
    result shouldBe a[ValContext]
  }

  it should "convert from object with public fields" in {
    case class Obj(val a: Int, val b: String)

    valueMapper.toVal(Obj(a = 2, b = "foo")) match {
      case ValContext(context) =>
        val variables = context.variableProvider.getVariables
        variables should be(Map("a" -> 2, "b" -> "foo"))
    }
  }

  it should "convert from object with public getters" in {
    case class Obj(private val a: Int, private val b: String) {
      def getA(): Int    = a
      def getB(): String = b
    }

    valueMapper.toVal(Obj(a = 2, b = "foo")) match {
      case ValContext(context) =>
        val variables = context.variableProvider.getVariables
        variables should be(Map("a" -> 2, "b" -> "foo"))
    }
  }

  it should "convert from object with public boolean getter" in {
    case class Obj(private val a: Boolean) {
      def isA(): Boolean = a
    }

    valueMapper.toVal(Obj(a = true)) match {
      case ValContext(context) =>
        val variables = context.variableProvider.getVariables
        variables should be(Map("a" -> true))
    }
  }

  it should "convert from Some" in {

    valueMapper.toVal(Some("foo")) should be(ValString("foo"))
  }

  it should "convert from None" in {

    valueMapper.toVal(None) should be(ValNull)
  }

  it should "convert from Enumeration" in {

    object WeekDay extends Enumeration {
      type WeekDay = Value
      val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
    }

    valueMapper.toVal(WeekDay.Mon) should be(ValString("Mon"))
    valueMapper.toVal(WeekDay.Fri) should be(ValString("Fri"))
  }

  it should "convert from null" in {

    valueMapper.toVal(null) should be(ValNull)
  }

}
