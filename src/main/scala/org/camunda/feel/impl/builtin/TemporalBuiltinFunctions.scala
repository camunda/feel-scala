package org.camunda.feel.impl.builtin

import org.camunda.feel.FeelEngineClock
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValDate, ValDateTime}

class TemporalBuiltinFunctions(clock: FeelEngineClock) {

  def functions = Map(
    "now" -> List(nowFunction),
    "today" -> List(todayFunction)
  )

  private def nowFunction = builtinFunction(
    params = List.empty,
    invoke = {
      case _ =>
        val now = clock.getCurrentTime
        ValDateTime(now)
    }
  )

  private def todayFunction = builtinFunction(
    params = List.empty,
    invoke = {
      case _ =>
        val today = clock.getCurrentTime.toLocalDate
        ValDate(today)
    }
  )

}
