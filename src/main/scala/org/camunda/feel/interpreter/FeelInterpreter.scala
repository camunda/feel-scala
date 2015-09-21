package org.camunda.feel.interpreter

import org.camunda.feel.parser._

/**
 * @author Philipp Ossler
 */
class FeelInterpreter {

  def test(expression: Exp)(implicit context: Context): Val = {

    // transform simple literal to simple positive unary test
    val exp = expression match {
      case x @ ConstNumber(_) => Equal(x)
      case x @ ConstBool(_) => Equal(x)
      case e => e
    }

    value(exp)
  }

  def value(expression: Exp)(implicit context: Context): Val = expression match {
    case ConstNumber(x) => ValNumber(x)
    case ConstBool(b) => ValBoolean(b)
    case LessThan(x) => withInput(value(x), _ < _, ValBoolean(_: Boolean))
    case LessOrEqual(x) => withInput(value(x), _ <= _, ValBoolean(_: Boolean))
    case exp => ValError(s"unsupported expression '$exp'")
  }

  private def withInput[T](x: Val, f: (Double, Double) => T, c: T => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => c(f(i, x)))
      case _ => ValError(s"expected Number but found '$input'")
    })

  private def withNumber(x: Val, f: Double => Val): Val = x match {
    case ValNumber(x) => f(x)
    case _ => ValError(s"expected Number but found '$x'")
  }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case ValError(e) => ValError(s"expected value but found '$e'")
    case _ => f(x)
  }

  private def input(implicit context: Context): Val = context.input
}

case class Context(in: Any) {

  def input: Val = in match {
    case (x: Int) => ValNumber(x)
    case _ => ValError(s"unsupported input '$in'")
  }
}