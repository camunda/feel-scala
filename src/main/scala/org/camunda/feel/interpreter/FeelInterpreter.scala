package org.camunda.feel.interpreter

import org.camunda.feel.parser._
import org.joda.time.LocalDate

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
    case ConstDate(d) => ValDate(d)
    case LessThan(x) => withInput(_ match {
      case ValNumber(i) => withNumber(value(x), x => ValBoolean(i < x))
      case ValDate(i) => withDate(value(x), x => ValBoolean(i.isBefore(x)))
      case e => ValError(s"expected number, date or time for operator '<' but found '$e'")
    })
    case LessOrEqual(x) => withInput(_ match {
      case ValNumber(i) => withNumber(value(x), x => ValBoolean(i <= x))
      case ValDate(i) => withDate(value(x), x => ValBoolean(i.isBefore(x) || i.equals(x)))
      case e => ValError(s"expected number, date or time for operator '<' but found '$e'")
    })
    case Interval(start, end) => withInput(_ match {
      case ValNumber(i) => withNumbers(value(start.value), value(end.value), (start,end) => ValBoolean( i > start && i < end ) )
      case e => ValError(s"expected number, date or time for interval but found '$e'")
    })
    case exp => ValError(s"unsupported expression '$exp'")
  }

  private def withInput[R, T](f: Val => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValError(e) => ValError(s"expected Number but found '$input'")
      case _ => f(input)
    })

  private def withNumbers(x: Val, y: Val, f: (Double, Double) => Val): Val =
    withNumber(x, x => {
      withNumber(y, y => {
        f(x, y)
      })
    })

  private def withNumber(x: Val, f: Double => Val): Val = x match {
    case ValNumber(x) => f(x)
    case _ => ValError(s"expected Number but found '$x'")
  }

  private def withDate(x: Val, f: LocalDate => Val): Val = x match {
    case ValDate(x) => f(x)
    case _ => ValError(s"expected Date but found '$x'")
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
    case (b: Boolean) => ValBoolean(b)
    case (d: LocalDate) => ValDate(d)
    case _ => ValError(s"unsupported input '$in'")
  }
}