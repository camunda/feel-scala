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

import org.camunda.feel.Number
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{
  Val,
  ValBoolean,
  ValError,
  ValNull,
  ValNumber,
  ValString,
  ValYearMonthDuration,
  ValDayTimeDuration
}

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object NumericBuiltinFunctions {

  def functions = Map(
    "decimal"         -> List(decimalFunction, decimalFunction3),
    "floor"           -> List(floorFunction, floorFunction2),
    "ceiling"         -> List(ceilingFunction, ceilingFunction2),
    "abs"             -> List(absFunction(paramName = "number"), absFunction(paramName = "n")),
    "modulo"          -> List(moduloFunction),
    "sqrt"            -> List(sqrtFunction),
    "log"             -> List(logFunction),
    "exp"             -> List(expFunction),
    "odd"             -> List(oddFunction),
    "even"            -> List(evenFunction),
    "round up"        -> List(roundUpFunction),
    "round down"      -> List(roundDownFunction),
    "round half up"   -> List(roundHalfUpFunction),
    "round half down" -> List(roundHalfDownFunction),
    "random number"   -> List(randomNumberFunction)
  )

  private def decimalFunction = builtinFunction(
    params = List("n", "scale"),
    invoke = { case List(ValNumber(n), ValNumber(scale)) =>
      round(n, scale, RoundingMode.HALF_EVEN)
    }
  )

  def decimalFunction3 = builtinFunction(
    params = List("n", "scale", "mode"),
    invoke = {
      case List(ValNumber(n), ValNumber(scale), ValString(mode)) if (isRoundingMode(mode)) => {
        val roundingMode = RoundingMode.withName(mode.toUpperCase)
        round(n, scale, roundingMode)
      }
      case List(ValNumber(_), ValNumber(_), ValString(mode))                               => {
        val roundingModes = RoundingMode.values.mkString(", ")
        ValError(s"Illegal argument '$mode' for rounding mode. Must be one of: $roundingModes")
      }
    }
  )

  private def floorFunction2 =
    builtinFunction(
      params = List("n", "scala"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.FLOOR)
      }
    )

  private def ceilingFunction2 =
    builtinFunction(
      params = List("n", "scale"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.CEILING)
      }
    )

  private def floorFunction =
    builtinFunction(
      params = List("n"),
      invoke = { case List(ValNumber(n)) =>
        round(n, 0, RoundingMode.FLOOR)
      }
    )

  private def ceilingFunction =
    builtinFunction(
      params = List("n"),
      invoke = { case List(ValNumber(n)) =>
        round(n, 0, RoundingMode.CEILING)
      }
    )

  private def isRoundingMode(mode: String) =
    RoundingMode.values.map(_.toString).contains(mode.toUpperCase)

  private def round(n: Number, scale: Number, roundingMode: RoundingMode.Value): Val = {
    try {
      val x = n.setScale(scale.intValue, roundingMode)
      ValNumber(x)
    } catch {
      case e: ArithmeticException =>
        ValError(s"Failed to apply rounding mode '$roundingMode': ${e.getMessage}")
    }
  }

  private def absFunction(paramName: String) =
    builtinFunction(
      params = List(paramName),
      invoke = {
        case List(ValNumber(n))                            => ValNumber(n.abs)
        case List(ValYearMonthDuration(n)) if n.isNegative =>
          ValYearMonthDuration(n.negated())
        case List(ValYearMonthDuration(n))                 => ValYearMonthDuration(n)
        case List(ValDayTimeDuration(n))                   => ValDayTimeDuration(n.abs())
      }
    )

  private def moduloFunction =
    builtinFunction(
      params = List("dividend", "divisor"),
      invoke = { case List(ValNumber(dividend), ValNumber(divisor)) =>
        ValNumber(((dividend % divisor) + divisor) % divisor)
      }
    )

  private def sqrtFunction =
    builtinFunction(
      params = List("number"),
      invoke = {
        case List(ValNumber(n)) if n < 0 => ValNull
        case List(ValNumber(n))          => ValNumber(Math.sqrt(n.toDouble))
      }
    )

  private def logFunction =
    builtinFunction(
      params = List("number"),
      invoke = { case List(ValNumber(n)) =>
        ValNumber(Math.log(n.toDouble))
      }
    )

  private def expFunction =
    builtinFunction(
      params = List("number"),
      invoke = { case List(ValNumber(n)) =>
        ValNumber(Math.exp(n.toDouble))
      }
    )

  private def oddFunction =
    builtinFunction(
      params = List("number"),
      invoke = { case List(ValNumber(n)) =>
        ValBoolean(n.abs % 2 == 1)
      }
    )

  private def evenFunction =
    builtinFunction(
      params = List("number"),
      invoke = { case List(ValNumber(n)) =>
        ValBoolean(n % 2 == 0)
      }
    )

  private def roundUpFunction =
    builtinFunction(
      params = List("n", "scale"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.UP)
      }
    )

  private def roundDownFunction =
    builtinFunction(
      params = List("n", "scale"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.DOWN)
      }
    )

  private def roundHalfUpFunction =
    builtinFunction(
      params = List("n", "scale"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.HALF_UP)
      }
    )

  private def roundHalfDownFunction =
    builtinFunction(
      params = List("n", "scala"),
      invoke = { case List(ValNumber(n), ValNumber(scale)) =>
        round(n, scale, RoundingMode.HALF_DOWN)
      }
    )

  private def randomNumberFunction =
    builtinFunction(
      params = List(),
      invoke = { case List() =>
        ValNumber(Random.nextDouble())
      }
    )
}
