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

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{
  Val,
  ValBoolean,
  ValDate,
  ValDateTime,
  ValDayTimeDuration,
  ValLocalDateTime,
  ValLocalTime,
  ValNumber,
  ValRange,
  ValTime,
  ValYearMonthDuration
}

import scala.annotation.tailrec

object RangeBuiltinFunction {
  def functions = Map(
    "before" -> List(
      beforeFunction("point1", "point2"),
      beforeFunction("point", "range"),
      beforeFunction("range", "point"),
      beforeFunction("range1", "range2")
    ),
    "after" -> List(
      afterFunction("point1", "point2"),
      afterFunction("point", "range"),
      afterFunction("range", "point"),
      afterFunction("range1", "range2")
    ),
    "meets" -> List(meetsFunction),
    "met by" -> List(metByFunction),
    "overlaps" -> List(overlapsFunction),
    "overlaps before" -> List(overlapsBeforeFunction),
    "overlaps after" -> List(overlapsAfterFunction),
    "finishes" -> List(
      finishesFunction("point", "range"),
      finishesFunction("range1", "range2")
    ),
    "finished by" -> List(
      finishedByFunction("point", "range"),
      finishedByFunction("range1", "range2")
    ),
    "includes" -> List(
      includesFunction("range", "point"),
      includesFunction("range1", "range2")
    ),
    "during" -> List(
      duringFunction("point", "range"),
      duringFunction("range1", "range2")
    ),
    "starts" -> List(
      starts("point", "range"),
      starts("range1", "range2")
    ),
    "started by" -> List(
      startedBy("range", "point"),
      startedBy("range1", "range2")
    ),
    "coincides" -> List(
      coincides("point1", "point2"),
      coincides("range1", "range2")
    )
  )

  private def rangeBuiltinFunction(params: (String, String),
                                   invoke: PartialFunction[(Val, Val), Any]) =
    builtinFunction(params = List(params._1, params._2), invoke = {
      case List(x, y) if isComparable(x, y) => invoke(x, y)
    })

  @tailrec
  private def isComparable(x: Val, y: Val): Boolean = (x, y) match {
    case (ValRange(start1, _), ValRange(start2, _)) =>
      isComparable(start1.value, start2.value)
    case (ValRange(start, _), point: Val) => isComparable(point, start.value)
    case (point: Val, ValRange(start, _)) => isComparable(point, start.value)
    case (point1: Val, point2: Val) =>
      isPointValue(point1) && isPointValue(point2) && point1.getClass == point2.getClass
    case _ => false
  }

  private def isPointValue(value: Val): Boolean = value match {
    case _: ValNumber            => true
    case _: ValDate              => true
    case _: ValTime              => true
    case _: ValLocalTime         => true
    case _: ValDateTime          => true
    case _: ValLocalDateTime     => true
    case _: ValYearMonthDuration => true
    case _: ValDayTimeDuration   => true
    case _                       => false
  }

  private def beforeFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(_, end1), ValRange(start2, _)) =>
          ValBoolean(
            end1.value < start2.value || (!end1.isClosed | !start2.isClosed) & end1.value == start2.value)
        case (point: Val, ValRange(start, _)) =>
          ValBoolean(
            point < start.value || (point == start.value & !start.isClosed))
        case (ValRange(_, end), point: Val) =>
          ValBoolean(end.value < point || (end.value == point & !end.isClosed))
        case (point1, point2) => ValBoolean(point1 < point2)
      }
    )

  private def afterFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, _), ValRange(_, end2)) =>
          ValBoolean(
            start1.value > end2.value || ((!start1.isClosed | !end2.isClosed) & start1.value == end2.value))
        case (point: Val, ValRange(_, end)) =>
          ValBoolean(point > end.value || (point == end.value & !end.isClosed))
        case (ValRange(start, _), point: Val) =>
          ValBoolean(
            start.value > point || (start.value == point & !start.isClosed))
        case (point1, point2) => ValBoolean(point1 > point2)
      }
    )

  private def meetsFunction =
    rangeBuiltinFunction(
      params = ("range1", "range2"),
      invoke = {
        case (ValRange(_, end1), ValRange(start2, _)) =>
          ValBoolean(
            end1.isClosed && start2.isClosed && end1.value == start2.value)
      }
    )

  private def metByFunction =
    rangeBuiltinFunction(
      params = ("range1", "range2"),
      invoke = {
        case (ValRange(start1, _), ValRange(_, end2)) =>
          ValBoolean(
            start1.isClosed && end2.isClosed && start1.value == end2.value)
      }
    )

  private def overlapsFunction =
    rangeBuiltinFunction(
      params = ("range1", "range2"),
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (end1.value > start2.value || (end1.value == start2.value && end1.isClosed && start2.isClosed)) && (start1.value < end2.value || (start1.value == end2.value && start1.isClosed && end2.isClosed))
          )
      }
    )

  private def overlapsBeforeFunction =
    rangeBuiltinFunction(
      params = ("range1", "range2"),
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start1.value < start2.value || (start1.value == start2.value && start1.isClosed && !start2.isClosed)) && (end1.value > start2.value || (end1.value == start2.value && end1.isClosed && start2.isClosed)) && (end1.value < end2.value || (end1.value == end2.value && (!end1.isClosed || end2.isClosed)))
          )
      }
    )

  private def overlapsAfterFunction =
    rangeBuiltinFunction(
      params = ("range1", "range2"),
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start2.value < start1.value || (start2.value == start1.value && start2.isClosed && !start1.isClosed)) && (end2.value > start1.value || (end2.value == start1.value && end2.isClosed && start1.isClosed)) && (end2.value < end1.value || (end2.value == end1.value && (!end2.isClosed || end1.isClosed)))
          )
      }
    )

  private def finishesFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            end1.isClosed == end2.isClosed && end1.value == end2.value && (start1.value > start2.value || (start1.value == start2.value && (!start1.isClosed || start2.isClosed)))
          )
        case (point: Val, ValRange(_, end)) =>
          ValBoolean(end.isClosed && end.value == point)
      }
    )

  private def finishedByFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            end1.isClosed == end2.isClosed && end1.value == end2.value && (start1.value < start2.value || (start1.value == start2.value && (start1.isClosed || !start2.isClosed)))
          )
        case (ValRange(_, end), point: Val) =>
          ValBoolean(
            end.isClosed && end.value == point
          )
      }
    )

  private def includesFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start1.value < start2.value || (start1.value == start2.value && (start1.isClosed || !start2.isClosed))) && (end1.value > end2.value || (end1.value == end2.value && (end1.isClosed || !end2.isClosed)))
          )
        case (ValRange(start, end), point: Val) =>
          ValBoolean(
            (start.value < point && end.value > point) || (start.value == point && start.isClosed) || (end.value == point && end.isClosed)
          )
      }
    )

  private def duringFunction(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start2.value < start1.value || (start2.value == start1.value && (start2.isClosed || !start1.isClosed))) && (end2.value > end1.value || (end2.value == end1.value && (end2.isClosed || !end1.isClosed)))
          )
        case (point: Val, ValRange(start, end)) =>
          ValBoolean(
            (start.value < point && end.value > point) || (start.value == point && start.isClosed) || (end.value == point && end.isClosed)
          )
      }
    )

  private def starts(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && (end1.value < end2.value || (end1.value == end2.value && (!end1.isClosed || end2.isClosed)))
          )
        case (point: Val, ValRange(start, _)) =>
          ValBoolean(start.value == point && start.isClosed)
      }
    )

  private def startedBy(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && (end2.value < end1.value || (end2.value == end1.value && (!end2.isClosed || end1.isClosed)))
          )
        case (ValRange(start, _), point: Val) =>
          ValBoolean(start.value == point && start.isClosed)
      }
    )

  private def coincides(params: (String, String)) =
    rangeBuiltinFunction(
      params = params,
      invoke = {
        case (ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && end1.value == end2.value && end1.isClosed == end2.isClosed
          )
        case (point1, point2) =>
          ValBoolean(point1 == point2)
      }
    )
}
