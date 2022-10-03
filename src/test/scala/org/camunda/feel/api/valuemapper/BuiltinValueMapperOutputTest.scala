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
package org.camunda.feel.api.valuemapper

import org.camunda.feel.FeelEngine
import org.camunda.feel.impl.JavaValueMapper
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

import java.time.{
  Duration,
  Instant,
  LocalDate,
  LocalDateTime,
  LocalTime,
  OffsetTime,
  Period,
  ZoneId,
  ZoneOffset,
  ZonedDateTime
}

class BuiltinValueMapperOutputTest extends AnyFlatSpec with Matchers {

  val engine =
    new FeelEngine(null, CompositeValueMapper(List(new JavaValueMapper())))

  "The value mapper" should "return string as java.lang.String" in {
    engine
      .evalExpression("\"foo\"")
      .map { result =>
        result shouldBe a[java.lang.String]
        result shouldBe "foo"
      }
  }

  it should "return (floating point) number as java.lang.Double" in {
    engine
      .evalExpression("10/3")
      .map { result =>
        result shouldBe a[java.lang.Double]
        result shouldBe (10.0 / 3.0)
      }
  }

  it should "return (whole) number as java.lang.Long" in {
    engine
      .evalExpression("10")
      .map { result =>
        result shouldBe a[java.lang.Long]
        result shouldBe 10
      }
  }

  it should "return boolean as java.lang.Boolean" in {
    engine
      .evalExpression("true")
      .map { result =>
        result shouldBe a[java.lang.Boolean]
        result should be
        true
      }
  }

  it should "return local date-time as java.time.LocalDateTime" in {
    engine
      .evalExpression(""" date and time("2019-08-12T22:22:22") """)
      .map {
        _ shouldBe LocalDateTime.parse("2019-08-12T22:22:22")
      }
  }

  it should "return date-time with zone offset as java.time.ZonedDateTime" in {
    engine
      .evalExpression(""" date and time("2019-08-12T22:22:22+02:00") """)
      .map {
        _ shouldBe ZonedDateTime.of(
          LocalDateTime.parse("2019-08-12T22:22:22"),
          ZoneOffset.ofHours(2)
        )
      }
  }

  it should "return date-time with zone id as java.time.ZonedDateTime" in {
    engine
      .evalExpression(
        """ date and time("2019-08-12T22:22:22@Europe/Berlin") """)
      .map {
        _ shouldBe ZonedDateTime.of(
          LocalDateTime.parse("2019-08-12T22:22:22"),
          ZoneId.of("Europe/Berlin")
        )
      }
  }

  it should "return time with zone id as java.time.OffsetTime" in {
    engine
      .evalExpression(""" time("22:22:22@Europe/Berlin") """)
      .map {
        _ shouldBe OffsetTime.of(
          LocalTime.parse("22:22:22"),
          ZoneId.of("Europe/Berlin").getRules.getStandardOffset(Instant.now)
        )
      }
  }

  it should "return time with zone offset as java.time.OffsetTime" in {
    engine
      .evalExpression(""" time("22:22:22+02:00") """)
      .map {
        _ shouldBe OffsetTime.of(
          LocalTime.parse("22:22:22"),
          ZoneOffset.ofHours(2)
        )
      }
  }

  it should "return local time as java.time.LocalTime" in {
    engine
      .evalExpression(""" time("22:22:22") """)
      .map {
        _ shouldBe LocalTime.parse("22:22:22")
      }
  }

  it should "return date as java.time.LocalDate" in {
    engine
      .evalExpression(""" date("2019-08-12") """)
      .map {
        _ shouldBe LocalDate.parse("2019-08-12")
      }
  }

  it should "return years-months-duration as java.time.Period" in {
    engine
      .evalExpression(""" duration("P1Y") """)
      .map {
        _ shouldBe Period.ofYears(1)
      }
  }

  it should "return days-time-duration as java.time.Duration" in {
    engine
      .evalExpression(""" duration("PT2H") """)
      .map {
        _ shouldBe Duration.ofHours(2)
      }
  }

  it should "return null as null" in {
    val nullValue: java.lang.Integer = null;

    engine
      .evalExpression("null")
      .map {
        _ shouldBe nullValue
      }
  }

  it should "return context as java.util.Map" in {
    engine
      .evalExpression("{foo: 42, bar: 5.5, baz: [1, 2]}")
      .map { result =>
        result shouldBe a[java.util.Map[_, _]]

        result match {
          case map: java.util.Map[String, Any] =>
            map.get("foo") shouldBe a[java.lang.Long]
            map.get("bar") shouldBe a[java.lang.Double]
            map.get("baz") shouldBe a[java.util.List[_]]

            map.get("baz") match {
              case list: java.util.List[Any] =>
                list.get(0) shouldBe a[java.lang.Long]
            }
        }
      }
  }

  it should "return list as java.util.List" in {
    engine
      .evalExpression(""" [6, 5.5, {"foo": "bar"}] """)
      .map { result =>
        result shouldBe a[java.util.List[_]]

        result match {
          case list: java.util.List[Any] =>
            list.get(0) shouldBe a[java.lang.Long]
            list.get(1) shouldBe a[java.lang.Double]
            list.get(2) shouldBe a[java.util.Map[_, _]]

            list.get(2) match {
              case map: java.util.Map[String, Any] =>
                map.get("foo") shouldBe a[java.lang.String]
            }
        }
      }
  }

}
