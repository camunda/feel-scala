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

object RangeBuiltinFunction {
  def functions = Map(
    "before" -> List(
      beforeFunction(List("point1", "point2")),
      beforeFunction(List("point", "range")),
      beforeFunction(List("range", "point")),
      beforeFunction(List("range1", "range2"))
    ),
    "after" -> List(
      afterFunction(List("point1", "point2")),
      afterFunction(List("point", "range")),
      afterFunction(List("range", "point")),
      afterFunction(List("range1", "range2"))
    ),
    "meets" -> List(meetsFunction),
    "met by" -> List(metByFunction),
    "overlaps" -> List(overlapsFunction),
    "overlaps before" -> List(overlapsBeforeFunction),
    "overlaps after" -> List(overlapsAfterFunction),
    "finishes" -> List(
      finishesFunction(List("point", "range")),
      finishesFunction(List("range1", "range2"))
    ),
    "finished by" -> List(
      finishedByFunction(List("point", "range")),
      finishedByFunction(List("range1", "range2"))
    ),
    "includes" -> List(
      includesFunction(List("range", "point")),
      includesFunction(List("range1", "range2"))
    ),
    "during" -> List(
      duringFunction(List("point", "range")),
      duringFunction(List("range1", "range2"))
    ),
    "starts" -> List(
      starts(List("point", "range")),
      starts(List("range1", "range2"))
    ),
    "started by" -> List(
      startedBy(List("range", "point")),
      startedBy(List("range1", "range2"))
    ),
    "coincides" -> List(
      coincides(List("point1", "point2")),
      coincides(List("range1", "range2"))
    )
  )

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

  private def beforeFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValRange(_, end1), ValRange(start2, _)) =>
          ValBoolean(
            end1.value < start2.value || (!end1.isClosed | !start2.isClosed) & end1.value == start2.value)
        case List(point: Val, ValRange(start, _)) if isPointValue(point) =>
          ValBoolean(
            point < start.value || (point == start.value & !start.isClosed))
        case List(ValRange(_, end), point: Val) if isPointValue(point) =>
          ValBoolean(end.value < point || (end.value == point & !end.isClosed))
        case List(point1, point2)
            if isPointValue(point1) && isPointValue(point2) =>
          ValBoolean(point1 < point2)
      }
    )

  private def afterFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point1), ValNumber(point2)) =>
          ValBoolean(point1 > point2)
        case List(point: ValNumber, ValRange(_, end)) =>
          ValBoolean(point > end.value || (point == end.value & !end.isClosed))
        case List(ValRange(start, _), point: ValNumber) =>
          ValBoolean(
            start.value > point || (start.value == point & !start.isClosed))
        case List(ValRange(start1, _), ValRange(_, end2)) =>
          ValBoolean(
            start1.value > end2.value || ((!start1.isClosed | !end2.isClosed) & start1.value == end2.value))
      }
    )

  private def meetsFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(_, end1), ValRange(start2, _)) =>
          ValBoolean(
            end1.isClosed && start2.isClosed && end1.value == start2.value)
      }
    )

  private def metByFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(start1, _), ValRange(_, end2)) =>
          ValBoolean(
            start1.isClosed && end2.isClosed && start1.value == end2.value)
      }
    )

  private def overlapsFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (end1.value > start2.value || (end1.value == start2.value && end1.isClosed && start2.isClosed)) && (start1.value < end2.value || (start1.value == end2.value && start1.isClosed && end2.isClosed))
          )
      }
    )

  private def overlapsBeforeFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start1.value < start2.value || (start1.value == start2.value && start1.isClosed && !start2.isClosed)) && (end1.value > start2.value || (end1.value == start2.value && end1.isClosed && start2.isClosed)) && (end1.value < end2.value || (end1.value == end2.value && (!end1.isClosed || end2.isClosed)))
          )
      }
    )

  private def overlapsAfterFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start2.value < start1.value || (start2.value == start1.value && start2.isClosed && !start1.isClosed)) && (end2.value > start1.value || (end2.value == start1.value && end2.isClosed && start1.isClosed)) && (end2.value < end1.value || (end2.value == end1.value && (!end2.isClosed || end1.isClosed)))
          )
      }
    )

  private def finishesFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(point: ValNumber, ValRange(_, end)) =>
          ValBoolean(end.isClosed && end.value == point)
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            end1.isClosed == end2.isClosed && end1.value == end2.value && (start1.value > start2.value || (start1.value == start2.value && (!start1.isClosed || start2.isClosed)))
          )
      }
    )

  private def finishedByFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValRange(_, end), point: ValNumber) =>
          ValBoolean(
            end.isClosed && end.value == point
          )
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            end1.isClosed == end2.isClosed && end1.value == end2.value && (start1.value < start2.value || (start1.value == start2.value && (start1.isClosed || !start2.isClosed)))
          )
      }
    )

  private def includesFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValRange(start, end), point: ValNumber) =>
          ValBoolean(
            (start.value < point && end.value > point) || (start.value == point && start.isClosed) || (end.value == point && end.isClosed)
          )
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start1.value < start2.value || (start1.value == start2.value && (start1.isClosed || !start2.isClosed))) && (end1.value > end2.value || (end1.value == end2.value && (end1.isClosed || !end2.isClosed)))
          )
      }
    )

  private def duringFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(point: ValNumber, ValRange(start, end)) =>
          ValBoolean(
            (start.value < point && end.value > point) || (start.value == point && start.isClosed) || (end.value == point && end.isClosed)
          )
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            (start2.value < start1.value || (start2.value == start1.value && (start2.isClosed || !start1.isClosed))) && (end2.value > end1.value || (end2.value == end1.value && (end2.isClosed || !end1.isClosed)))
          )
      }
    )

  private def starts(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(point: ValNumber, ValRange(start, _)) =>
          ValBoolean(start.value == point && start.isClosed)
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && (end1.value < end2.value || (end1.value == end2.value && (!end1.isClosed || end2.isClosed)))
          )
      }
    )

  private def startedBy(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValRange(start, _), point: ValNumber) =>
          ValBoolean(start.value == point && start.isClosed)
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && (end2.value < end1.value || (end2.value == end1.value && (!end2.isClosed || end1.isClosed)))
          )
      }
    )

  private def coincides(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point1), ValNumber(point2)) =>
          ValBoolean(point1 == point2)
        case List(ValRange(start1, end1), ValRange(start2, end2)) =>
          ValBoolean(
            start1.value == start2.value && start1.isClosed == start2.isClosed && end1.value == end2.value && end1.isClosed == end2.isClosed
          )
      }
    )
}
