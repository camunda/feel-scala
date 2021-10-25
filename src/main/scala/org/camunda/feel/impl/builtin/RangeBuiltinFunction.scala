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
    "overlaps before" -> List(),
    "overlaps after" -> List(),
    "finishes" -> List(),
    "finished by" -> List(),
    "includes" -> List(),
    "during" -> List(),
    "starts" -> List(),
    "started by" -> List(),
    "coincides" -> List()
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
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean(range1.endIncl && range2.startIncl && range1.end == range2.start)
    })

  private def metByFunction =
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean(range1.startIncl && range2.endIncl && range1.start == range2.end)
    })

  private def overlapsFunction =
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean((range1.end > range2.start || (range1.end == range2.start && (range1.endIncl || range2.endIncl))) & (range1.start < range2.end | (range1.start == range2.end & range1.startIncl & range2.endIncl)))
    })

  private def overlapsBeforeFunction =
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean(true)
    })

  private def overlapsAfterFunction =
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean(false)
    })

  private def finishesFunction =
    builtinFunction(params = List("range1", "range2"), invoke = {
      case List(ValRange(range1), ValRange(range2)) =>
        ValBoolean((range1.end > range2.start || (range1.end == range2.start && (range1.endIncl || range2.endIncl))) & (range1.start < range2.end | (range1.start == range2.end & range1.startIncl & range2.endIncl)))
    })

}
