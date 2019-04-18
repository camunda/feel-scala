package org.camunda.feel.example.spi

import org.camunda.feel.interpreter._
import org.camunda.feel.spi.CustomFunctionProvider
import scala.math.BigDecimal.int2bigDecimal

class CustomScalaFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): Option[ValFunction] = functions.get(name)

  val functions: Map[String, ValFunction] = Map(
    "foo" -> ValFunction(
      params = List("x"),
      invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
    ),
    "isBlack" -> ValFunction(
      params = List("s"),
      invoke = {
        case List(ValString(s)) =>
          if (s == "black") ValBoolean(true) else ValBoolean(false)
      },
    )
  )

}
