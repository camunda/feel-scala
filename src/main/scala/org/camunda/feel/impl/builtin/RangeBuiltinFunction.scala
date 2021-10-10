package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValBoolean, ValNumber, ValRange, ValString}

object RangeBuiltinFunction {
  def functions = Map(
    "before" -> List(BeforeFunction),
    "after" -> List(AfterFunction),
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

  private def BeforeFunction =
    builtinFunction(params = List("valOne", "valTwo"), invoke = {
      case List(ValNumber(valOne), ValNumber(valTwo)) => ValBoolean(valOne < valTwo)
      case List(ValNumber(valOne), ValString(valTwo)) => ValBoolean(valOne.toInt < ValRange(valTwo).start || (valOne.toInt == ValRange(valTwo).start & !ValRange(valTwo).startIncl))
      case List(ValString(valOne), ValNumber(valTwo)) => ValBoolean(ValRange(valOne).end < valTwo.toInt || (ValRange(valOne).end == valTwo.toInt & !ValRange(valOne).endIncl))
      case List(ValString(valOne), ValString(valTwo)) => ValBoolean(ValRange(valOne).end < ValRange(valTwo).start || ((!ValRange(valOne).endIncl | !ValRange(valTwo).startIncl) & ValRange(valOne).end == ValRange(valTwo).start))
    })

  private def AfterFunction =
    builtinFunction(params = List("valOne", "valTwo"), invoke = {
      case List(ValNumber(valOne), ValNumber(valTwo)) => ValBoolean(valOne > valTwo)
      case List(ValNumber(valOne), ValString(valTwo)) => ValBoolean(valOne.toInt > ValRange(valTwo).end || (valOne.toInt == ValRange(valTwo).end & !ValRange(valTwo).endIncl))
      case List(ValString(valOne), ValNumber(valTwo)) => ValBoolean(ValRange(valOne).start > valTwo.toInt || (ValRange(valOne).start == valTwo.toInt & !ValRange(valOne).startIncl))
      case List(ValString(valOne), ValString(valTwo)) => ValBoolean(ValRange(valOne).start > ValRange(valTwo).end || ((!ValRange(valOne).startIncl | !ValRange(valTwo).endIncl) & ValRange(valOne).start == ValRange(valTwo).end))
    })
}
