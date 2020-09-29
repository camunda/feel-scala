package org.camunda.feel.impl.builtin

import org.camunda.feel.Number
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._

import scala.math.BigDecimal.RoundingMode
import scala.math.Numeric.BigDecimalAsIfIntegral.abs

object NumericBuiltinFunctions {

  def functions = Map(
    "decimal" -> List(decimalFunction, decimalFunction3),
    "floor" -> List(floorFunction),
    "ceiling" -> List(ceilingFunction),
    "abs" -> List(absFunction),
    "modulo" -> List(moduloFunction),
    "sqrt" -> List(sqrtFunction),
    "log" -> List(logFunction),
    "exp" -> List(expFunction),
    "odd" -> List(oddFunction),
    "even" -> List(evenFunction)
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

  private def absFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValNumber(n.abs)
    })

  private def moduloFunction =
    builtinFunction(params = List("dividend", "divisor"), invoke = {
      case List(ValNumber(dividend), ValNumber(divisor)) =>
        ValNumber(dividend % divisor)
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
      case List(ValNumber(n)) => ValBoolean(abs(n) % 2 == 1)
    })

  private def evenFunction =
    builtinFunction(params = List("number"), invoke = {
      case List(ValNumber(n)) => ValBoolean(n % 2 == 0)
    })

}
