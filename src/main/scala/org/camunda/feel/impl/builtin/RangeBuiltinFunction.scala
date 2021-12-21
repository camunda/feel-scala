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
      beforeFunction("point1", "point2"),
      beforeFunction("point", "range"),
      beforeFunction("range", "point"),
      beforeFunction("range1", "range2")
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

  private def rangeBuiltinFunction(params: (String, String),
                                   invoke: PartialFunction[(Val, Val), Any]) =
    builtinFunction(params = List(params._1, params._2), invoke = {
      case List(x, y) if isComparable(x, y) => invoke(x, y)
    })

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
