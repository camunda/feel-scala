package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValBoolean, ValNumber, ValRange}

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
    "finished by" -> List(),
    "includes" -> List(),
    "during" -> List(),
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

  private def beforeFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point1), ValNumber(point2)) =>
          ValBoolean(point1 < point2)
        case List(ValNumber(point), ValRange(range)) =>
          ValBoolean(
            point.toInt < range.start || (point.toInt == range.start & !range.startIncl))
        case List(ValRange(range), ValNumber(point)) =>
          ValBoolean(
            range.end < point.toInt || (range.end == point.toInt & !range.endIncl))
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.end < range2.start || (!range1.endIncl | !range2.startIncl) & range1.end == range2.start)
      }
    )

  private def afterFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point1), ValNumber(point2)) =>
          ValBoolean(point1 > point2)
        case List(ValNumber(point), ValRange(range)) =>
          ValBoolean(
            point.toInt > range.end || (point.toInt == range.end & !range.endIncl))
        case List(ValRange(range), ValNumber(point)) =>
          ValBoolean(
            range.start > point.toInt || (range.start == point.toInt & !range.startIncl))
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.start > range2.end || ((!range1.startIncl | !range2.endIncl) & range1.start == range2.end))
      }
    )

  private def meetsFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.endIncl && range2.startIncl && range1.end == range2.start)
      }
    )

  private def metByFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.startIncl && range2.endIncl && range1.start == range2.end)
      }
    )

  private def overlapsFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            (range1.end > range2.start || (range1.end == range2.start && range1.endIncl && range2.startIncl)) && (range1.start < range2.end || (range1.start == range2.end && range1.startIncl && range2.endIncl)))
      }
    )

  private def overlapsBeforeFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            (range1.start < range2.start || (range1.start == range2.start && range1.startIncl && !range2.startIncl)) && (range1.end > range2.start || (range1.end == range2.start && range1.endIncl && range2.startIncl)) && (range1.end < range2.end || (range1.end == range2.end && (!range1.endIncl || range2.endIncl))))
      }
    )

  private def overlapsAfterFunction =
    builtinFunction(
      params = List("range1", "range2"),
      invoke = {
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            (range2.start < range1.start || (range2.start == range1.start && range2.startIncl && !range1.startIncl)) && (range2.end > range1.start || (range2.end == range1.start && range2.endIncl && range1.startIncl)) && (range2.end < range1.end || (range2.end == range1.end && (!range2.endIncl || range1.endIncl))))
      }
    )

  private def finishesFunction(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point), ValRange(range)) =>
          ValBoolean(range.endIncl && range.end == point)
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.endIncl == range2.endIncl && range1.end == range2.end && (range1.start > range2.start || (range1.start == range2.start && (!range1.startIncl || range2.startIncl))))
      }
    )

  private def starts(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point), ValRange(range)) =>
          ValBoolean(range.start == point && range.startIncl)
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.start == range2.start && range1.startIncl == range2.startIncl && (range1.end < range2.end || (range1.end == range2.end && (!range1.endIncl || range2.endIncl)))
          )
      }
    )

  private def startedBy(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValRange(range), ValNumber(point)) =>
          ValBoolean(range.start == point && range.startIncl)
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.start == range2.start && range1.startIncl == range2.startIncl && (range2.end < range1.end || (range2.end == range1.end && (!range2.endIncl || range1.endIncl))))
      }
    )

  private def coincides(params: List[String]) =
    builtinFunction(
      params = params,
      invoke = {
        case List(ValNumber(point1), ValNumber(point2)) =>
          ValBoolean(point1 == point2)
        case List(ValRange(range1), ValRange(range2)) =>
          ValBoolean(
            range1.start == range2.start && range1.startIncl == range2.startIncl && range1.end == range2.end && range1.endIncl == range2.endIncl)
      }
    )
}
