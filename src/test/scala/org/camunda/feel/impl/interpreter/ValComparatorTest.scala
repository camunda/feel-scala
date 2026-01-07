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
import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.collection.immutable.ListMap

class ValComparatorTest extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {

  private val comparator = new ValComparator(ValueMapper.defaultValueMapper)

  private def assertEqualsAndHashCode(x: Val, y: Val): Unit = {
    comparator.equals(x, y) shouldBe true
    comparator.hashCode(x) shouldBe comparator.hashCode(y)
  }

  private def assertNotEquals(x: Val, y: Val): Unit = {
    comparator.equals(x, y) shouldBe false
  }

  "ValComparator.compare" should "compare null values" in {
    comparator.compare(ValNull, ValNull) shouldBe ValBoolean(true)
    comparator.compare(ValNull, ValNumber(1)) shouldBe ValBoolean(false)
    comparator.compare(ValNumber(1), ValNull) shouldBe ValBoolean(false)

    // ValError is treated as null by Val#toOption
    comparator.compare(ValNull, ValError("x")) shouldBe ValBoolean(true)
    comparator.compare(ValError("x"), ValNull) shouldBe ValBoolean(true)

    // ValFatalError is not treated as null (toOption returns Some)
    comparator.compare(ValNull, ValFatalError("x")) shouldBe ValBoolean(false)
    comparator.compare(ValFatalError("x"), ValNull) shouldBe ValBoolean(false)
  }

  it should "compare equal simple FEEL values" in {
    val equalValues = Table(
      ("x", "y"),
      (ValNumber(1), ValNumber(1)),
      (ValBoolean(true), ValBoolean(true)),
      (ValString("a"), ValString("a")),
      (ValDate("2017-04-02"), ValDate("2017-04-02")),
      (ValLocalTime("12:04:30"), ValLocalTime("12:04:30")),
      (ValTime("12:04:30+01:00"), ValTime("12:04:30+01:00")),
      (ValLocalDateTime("2017-04-02T12:04:30"), ValLocalDateTime("2017-04-02T12:04:30")),
      (ValDateTime("2017-04-02T12:04:30+01:00"), ValDateTime("2017-04-02T12:04:30+01:00")),
      (ValYearMonthDuration("P2Y4M"), ValYearMonthDuration("P2Y4M")),
      (ValDayTimeDuration("PT4H22M"), ValDayTimeDuration("PT4H22M"))
    )

    forAll(equalValues) { (x, y) =>
      assertEqualsAndHashCode(x, y)
    }
  }

  it should "compare different simple FEEL values" in {
    val notEqualValues = Table(
      ("x", "y"),
      (ValNumber(1), ValNumber(2)),
      (ValBoolean(true), ValBoolean(false)),
      (ValString("a"), ValString("b")),
      (ValDate("2017-04-02"), ValDate("2017-04-03")),
      (ValLocalTime("12:04:30"), ValLocalTime("12:04:31")),
      (ValTime("12:04:30+01:00"), ValTime("12:04:30+02:00")),
      (ValLocalDateTime("2017-04-02T12:04:30"), ValLocalDateTime("2017-04-02T12:04:31")),
      (ValDateTime("2017-04-02T12:04:30+01:00"), ValDateTime("2017-04-02T12:04:30+02:00")),
      (ValYearMonthDuration("P2Y4M"), ValYearMonthDuration("P2Y5M")),
      (ValDayTimeDuration("PT4H22M"), ValDayTimeDuration("PT4H23M"))
    )

    forAll(notEqualValues) { (x, y) =>
      assertNotEquals(x, y)
    }
  }

  it should "compare equal lists (including nested lists)" in {
    val equalLists = Table(
      ("x", "y"),
      (
        ValList(Seq(ValNumber(1), ValString("a"))),
        ValList(Seq(ValNumber(1), ValString("a")))
      ),
      (
        ValList(Seq(ValList(Seq(ValNumber(1))), ValList(Seq(ValString("x"))))),
        ValList(Seq(ValList(Seq(ValNumber(1))), ValList(Seq(ValString("x")))))
      )
    )

    forAll(equalLists) { (x, y) =>
      assertEqualsAndHashCode(x, y)
    }
  }

  it should "compare different lists" in {
    val notEqualLists = Table(
      ("x", "y"),
      (
        ValList(Seq(ValNumber(1), ValString("a"))),
        ValList(Seq(ValNumber(1), ValString("b")))
      ),
      (
        ValList(Seq(ValNumber(1), ValString("a"))),
        ValList(Seq(ValString("a"), ValNumber(1)))
      ),
      (
        ValList(Seq(ValNumber(1), ValString("a"))),
        ValList(Seq(ValNumber(1), ValString("a"), ValString("x")))
      ),
      (
        ValList(Seq(ValList(Seq(ValNumber(1))), ValList(Seq(ValString("x"))))),
        ValList(Seq(ValList(Seq(ValNumber(2))), ValList(Seq(ValString("x")))))
      )
    )

    forAll(notEqualLists) { (x, y) =>
      assertNotEquals(x, y)
    }
  }

  it should "compare contexts using ValueMapper.toVal" in {
    val x = ValContext(Context.StaticContext(Map("a" -> 1, "b" -> "x")))
    val y = ValContext(Context.StaticContext(Map("a" -> 1, "b" -> "x")))
    val z = ValContext(Context.StaticContext(Map("a" -> 1, "b" -> "y")))

    assertEqualsAndHashCode(x, y)
    assertNotEquals(x, z)

    val nestedX = ValContext(Context.StaticContext(Map("a" -> Map("x" -> 1))))
    val nestedY = ValContext(Context.StaticContext(Map("a" -> Map("x" -> 1))))
    val nestedZ = ValContext(Context.StaticContext(Map("a" -> Map("x" -> 2))))

    assertEqualsAndHashCode(nestedX, nestedY)
    assertNotEquals(nestedX, nestedZ)

    val differentKeys = ValContext(Context.StaticContext(Map("a" -> 1)))
    assertNotEquals(x, differentKeys)
  }

  it should "return an error for values of different types" in {
    val comparisons = Table(
      ("x", "y", "expected"),
      (ValNumber(1), ValBoolean(true), ValError("Can't compare '1' with 'true'")),
      (ValNumber(1), ValString("a"), ValError("Can't compare '1' with '\"a\"'")),
      (ValNumber(1), ValDayTimeDuration("P1D"), ValError("Can't compare '1' with 'P1D'"))
    )

    forAll(comparisons) { (x, y, expected) =>
      comparator.compare(x, y) shouldBe expected
    }
  }

  it should "return an error for unsupported Val types" in {
    val range1 = ValRange(
      start = ClosedRangeBoundary(ValNumber(1)),
      end = OpenRangeBoundary(ValNumber(3))
    )
    val range2 = ValRange(
      start = ClosedRangeBoundary(ValNumber(1)),
      end = OpenRangeBoundary(ValNumber(3))
    )

    comparator.compare(range1, range2) shouldBe ValError(s"Can't compare '$range1' with '$range2'")

    val invokeFn: List[Val] => Any = _ => 1
    val fn1                        = ValFunction(params = List("a"), invoke = invokeFn)
    val fn2                        = ValFunction(params = List("a"), invoke = invokeFn)

    comparator.compare(fn1, fn2) shouldBe ValError(s"Can't compare '$fn1' with '$fn2'")

    comparator.compare(ValError("boom"), ValError("boom")) shouldBe ValError(
      "Can't compare 'error(\"boom\")' with 'error(\"boom\")'"
    )

    comparator.compare(ValFatalError("boom"), ValFatalError("boom")) shouldBe ValError(
      "Can't compare 'fatal error(\"boom\")' with 'fatal error(\"boom\")'"
    )
  }

  "ValComparator.hashCode" should "compute hash codes for null-like values" in {
    comparator.hashCode(ValNull) shouldBe 0
    comparator.hashCode(ValError("boom")) shouldBe 0
  }

  it should "compute hash codes for supported Val types" in {
    val supportedValues = Table(
      ("x", "y"),
      (ValNumber(1), ValNumber(1)),
      (ValBoolean(true), ValBoolean(true)),
      (ValString("a"), ValString("a")),
      (ValDate("2017-04-02"), ValDate("2017-04-02")),
      (ValLocalTime("12:04:30"), ValLocalTime("12:04:30")),
      (ValTime("12:04:30+01:00"), ValTime("12:04:30+01:00")),
      (ValLocalDateTime("2017-04-02T12:04:30"), ValLocalDateTime("2017-04-02T12:04:30")),
      (ValDateTime("2017-04-02T12:04:30+01:00"), ValDateTime("2017-04-02T12:04:30+01:00")),
      (ValYearMonthDuration("P2Y4M"), ValYearMonthDuration("P2Y4M")),
      (ValDayTimeDuration("PT4H22M"), ValDayTimeDuration("PT4H22M")),
      (ValList(Seq(ValNumber(1), ValString("a"))), ValList(Seq(ValNumber(1), ValString("a")))),
      (
        ValContext(Context.StaticContext(Map("a" -> 1, "b" -> "x"))),
        ValContext(Context.StaticContext(Map("a" -> 1, "b" -> "x")))
      )
    )

    forAll(supportedValues) { (x, y) =>
      assertEqualsAndHashCode(x, y)
    }
  }

  it should "compute the same hash code for contexts with key order differences" in {
    val ctx1 = ValContext(
      Context.StaticContext(
        ListMap(
          "a" -> 1,
          "b" -> "x",
          "c" -> List(1, 2, 3),
          "d" -> Map("nested" -> Map("x" -> 1, "y" -> "z")),
          "e" -> true
        )
      )
    )

    val ctx2 = ValContext(
      Context.StaticContext(
        ListMap(
          "e" -> true,
          "d" -> Map("nested" -> Map("y" -> "z", "x" -> 1)),
          "c" -> List(1, 2, 3),
          "b" -> "x",
          "a" -> 1
        )
      )
    )

    comparator.hashCode(ctx1) shouldBe comparator.hashCode(ctx2)
  }

  it should "compute hash codes for unsupported Val types" in {
    val range1 = ValRange(
      start = ClosedRangeBoundary(ValNumber(1)),
      end = OpenRangeBoundary(ValNumber(3))
    )
    val range2 = ValRange(
      start = ClosedRangeBoundary(ValNumber(1)),
      end = OpenRangeBoundary(ValNumber(3))
    )
    comparator.hashCode(range1) shouldBe comparator.hashCode(range2)

    val invokeFn: List[Val] => Any = _ => 1
    val fn1                        = ValFunction(params = List("a"), invoke = invokeFn)
    val fn2                        = ValFunction(params = List("a"), invoke = invokeFn)
    comparator.hashCode(fn1) shouldBe comparator.hashCode(fn2)

    // ValError behaves like null in ValComparator.hashCode
    comparator.hashCode(ValError("boom")) shouldBe 0
    comparator.hashCode(ValFatalError("boom")) shouldBe ValFatalError("boom").hashCode()
  }

}
