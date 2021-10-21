package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValBoolean, ValNumber, ValRange}

object RangeBuiltinFunction {
  def functions = Map(
    "before" -> List(beforeFunction),
    "after" -> List(afterFunction),
    "meets" -> List(),
    "met by" -> List(),
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

  private def beforeFunction =
    builtinFunction(params = List("valOne", "valTwo"), invoke = {
      case List(ValNumber(valOne), ValNumber(valTwo)) => ValBoolean(valOne < valTwo)
      case List(ValNumber(valOne), ValRange(valTwo)) => ValBoolean(valOne.toInt < valTwo.start || (valOne.toInt == valTwo.start & !valTwo.startIncl))
      case List(ValRange(valOne), ValNumber(valTwo)) => ValBoolean(valOne.end < valTwo.toInt || (valOne.end == valTwo.toInt & !valOne.endIncl))
      case List(ValRange(valOne), ValRange(valTwo)) => ValBoolean(valOne.end < valTwo.start || (!valOne.endIncl | !valTwo.startIncl) & valOne.end == valTwo.start)
    })

  private def afterFunction =
    builtinFunction(params = List("valOne", "valTwo"), invoke = {
      case List(ValNumber(valOne), ValNumber(valTwo)) => ValBoolean(valOne > valTwo)
      case List(ValNumber(valOne), ValRange(valTwo)) => ValBoolean(valOne.toInt > valTwo.end || (valOne.toInt == valTwo.end & !valTwo.endIncl))
      case List(ValRange(valOne), ValNumber(valTwo)) => ValBoolean(valOne.start > valTwo.toInt || (valOne.start == valTwo.toInt & !valOne.startIncl))
      case List(ValRange(valOne), ValRange(valTwo)) => ValBoolean(valOne.start > valTwo.end || ((!valOne.startIncl | !valTwo.endIncl) & valOne.start == valTwo.end))
    })
}
