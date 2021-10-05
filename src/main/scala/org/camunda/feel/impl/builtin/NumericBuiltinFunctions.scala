package org.camunda.feel.impl.builtin

import org.camunda.feel.Number
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.RoundingMode

object NumericBuiltinFunctions {

  def functions = Map(
    "decimal" -> List(decimalFunction, decimalFunction3),
    "floor" -> List(floorFunction, floorFunction2),
    "ceiling" -> List(ceilingFunction, ceilingFunction2),
    "abs" -> List(absFunction(paramName = "number"), absFunction(paramName = "n")),
    "modulo" -> List(moduloFunction),
    "sqrt" -> List(sqrtFunction),
    "log" -> List(logFunction),
    "exp" -> List(expFunction),
    "odd" -> List(oddFunction),
    "even" -> List(evenFunction),
    "round up" -> List(roundUpFunction),
    "round down" -> List(roundDownFunction),
    "round half up" -> List(roundHalfUpFunction),
    "round half down" -> List(roundHalfDownFunction)
  )

  private def decimalFunction = builtinFunction(
    params = List("n", "scale"),
    invoke = {
      case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.HALF_EVEN)
    }
  )

  def decimalFunction3 = builtinFunction(
    params = List("n", "scale", "mode"),
    invoke = {
      case List(ValNumber(n), ValNumber(scale), ValString(mode))
          if (isRoundingMode(mode)) => {
        val roundingMode = RoundingMode.withName(mode.toUpperCase)
        round(n, scale, roundingMode)
      }
      case List(ValNumber(_), ValNumber(_), ValString(mode)) => {
        val roundingModes = RoundingMode.values.mkString(", ")
        ValError(
          s"Illegal argument '$mode' for rounding mode. Must be one of: $roundingModes")
      }
    }
  )

  private def floorFunction2 =
    builtinFunction(params = List("n", "scala"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) => round(n, scale, RoundingMode.FLOOR)
    })

  private def ceilingFunction2 =
    builtinFunction(params = List("n", "scale"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) => round(n, scale, RoundingMode.CEILING)
    })

  private def floorFunction =
    builtinFunction(params = List("n"), invoke = {
      case List(ValNumber(n)) => round(n, 0, RoundingMode.FLOOR)
    })

  private def ceilingFunction =
    builtinFunction(params = List("n"), invoke = {
      case List(ValNumber(n)) => round(n, 0, RoundingMode.CEILING)
    })

  private def isRoundingMode(mode: String) =
    RoundingMode.values.map(_.toString).contains(mode.toUpperCase)

  private def round(n: Number,
                    scale: Number,
                    roundingMode: RoundingMode.Value): Val = {
    try {
      val x = n.setScale(scale.intValue, roundingMode)
      ValNumber(x)
    } catch {
      case e: ArithmeticException =>
        ValError(
          s"Failed to apply rounding mode '$roundingMode': ${e.getMessage}")
    }
  }

  private def absFunction(paramName: String) =
    builtinFunction(params = List(paramName), invoke = {
      case List(ValNumber(n)) => ValNumber(n.abs)
    })

  private def moduloFunction =
    builtinFunction(params = List("dividend", "divisor"), invoke = {
      case List(ValNumber(dividend), ValNumber(divisor)) =>
        ValNumber(((dividend % divisor) + divisor) % divisor)
    })

  private def sqrtFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) if n < 0 => ValNull
      case List(ValNumber(n))          => ValNumber(Math.sqrt(n.toDouble))
    })

  private def logFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValNumber(Math.log(n.toDouble))
    })

  private def expFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValNumber(Math.exp(n.toDouble))
    })

  private def oddFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValBoolean(n.abs % 2 == 1)
    })

  private def evenFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValBoolean(n % 2 == 0)
    })

  private def roundUpFunction =
    builtinFunction(params = List("n", "scale"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.UP)
    })

  private def roundDownFunction =
    builtinFunction(params = List("n", "scale"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.DOWN)
    })

  private def roundHalfUpFunction =
    builtinFunction(params = List("n", "scale"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.HALF_UP)
    })

  private def roundHalfDownFunction =
    builtinFunction(params = List("n", "scala"), invoke = {
      case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.HALF_DOWN)
    })
}
