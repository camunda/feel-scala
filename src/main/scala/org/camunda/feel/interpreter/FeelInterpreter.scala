package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser._

/**
 * @author Philipp Ossler
 */
class FeelInterpreter {

  def value(expression: Exp)(implicit context: Context = Context()): Val = expression match {
    // simple literals
    case ConstNumber(x) => ValNumber(x)
    case ConstBool(b) => ValBoolean(b)
    case ConstString(s) => ValString(s)
    case ConstDate(d) => ValDate(d)
    // simple unary tests
    case Equal(x) => unaryOpAny(value(x), _ == _, ValBoolean)
    case LessThan(x) => unaryOp(value(x), _ < _, ValBoolean)
    case LessOrEqual(x) => unaryOp(value(x), _ <= _, ValBoolean)
    case GreaterThan(x) => unaryOp(value(x), _ > _, ValBoolean)
    case GreaterOrEqual(x) => unaryOp(value(x), _ >= _, ValBoolean)
    case interval @ Interval(start, end) => unaryOpDual(value(start.value), value(end.value), isInInterval(interval), ValBoolean)
    // combinators
    case AtLeastOne(xs) => atLeastOne(xs, ValBoolean)
    case Not(x) => withBoolean(value(x), x => ValBoolean(!x))
    // context access
    case Ref(name) => context(name)
    // unsupported expression
    case exp => ValError(s"unsupported expression '$exp'")
  }

  private def unaryOpAny(x: Val, c: (Any, Any) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => f(c(i, x)))
      case ValDate(i) => withDate(x, x => f(c(i, x)))
      case ValBoolean(i) => withBoolean(x, x => f(c(i, x)))
      case ValString(i) => withString(x, x => f(c(i, x)))
      case _ => ValError(s"expected Number, Boolean, String or Date but found '$input'")
    })

  private def unaryOp(x: Val, c: (Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumber(x, x => f(c(i, x)))
      case ValDate(i) => withDate(x, x => f(c(i, x)))
      case _ => ValError(s"expected Number or Date but found '$input'")
    })

  private def unaryOpDual(x: Val, y: Val, c: (Compareable[_], Compareable[_], Compareable[_]) => Boolean, f: Boolean => Val)(implicit context: Context): Val =
    withVal(input, _ match {
      case ValNumber(i) => withNumbers(x, y, (x, y) => f(c(i, x, y)))
      case ValDate(i) => withDates(x, y, (x, y) => f(c(i, x, y)))
      case _ => ValError(s"expected Number or Date but found '$input'")
    })

  private def withNumbers(x: Val, y: Val, f: (Number, Number) => Val): Val =
    withNumber(x, x => {
      withNumber(y, y => {
        f(x, y)
      })
    })

  private def withNumber(x: Val, f: Number => Val): Val = x match {
    case ValNumber(x) => f(x)
    case _ => ValError(s"expected Number but found '$x'")
  }

  private def withDates(x: Val, y: Val, f: (Date, Date) => Val): Val =
    withDate(x, x => {
      withDate(y, y => {
        f(x, y)
      })
    })

  private def withBoolean(x: Val, f: Boolean => Val): Val = x match {
    case ValBoolean(x) => f(x)
    case _ => ValError(s"expected Boolean but found '$x'")
  }

  private def withString(x: Val, f: String => Val): Val = x match {
    case ValString(x) => f(x)
    case _ => ValError(s"expected String but found '$x'")
  }

  private def withDate(x: Val, f: Date => Val): Val = x match {
    case ValDate(x) => f(x)
    case _ => ValError(s"expected Date but found '$x'")
  }

  private def withVal(x: Val, f: Val => Val): Val = x match {
    case ValError(e) => ValError(s"expected value but found '$e'")
    case _ => f(x)
  }

  private def isInInterval(interval: Interval): (Compareable[_], Compareable[_], Compareable[_]) => Boolean =
    (i, x, y) => {
      val inStart: Boolean = interval.start match {
        case OpenIntervalBoundary(_) => i > x
        case ClosedIntervalBoundary(_) => i >= x
      }
      val inEnd = interval.end match {
        case OpenIntervalBoundary(_) => i < y
        case ClosedIntervalBoundary(_) => i <= y
      }
      inStart && inEnd
    }

  private def atLeastOne(xs: List[Exp], f: Boolean => Val)(implicit context: Context): Val = xs match {
    case Nil => f(false)
    case x :: xs => withBoolean(value(x), _ match {
      case true => f(true)
      case false => atLeastOne(xs, f)
    })
  }

  private def input(implicit context: Context): Val = context.input
  
}
