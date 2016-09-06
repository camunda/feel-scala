package org.camunda.feel.parser

import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
sealed trait Exp

case class ConstNumber(value: Number) extends Exp

case class ConstBool(value: Boolean) extends Exp

case class ConstString(value: String) extends Exp

case class ConstDate(value: Date) extends Exp

case class ConstTime(value: Time) extends Exp

case class ConstDuration(value: Duration) extends Exp

case object ConstNull extends Exp

case class InputLessThan(x: Exp) extends Exp

case class InputLessOrEqual(x: Exp) extends Exp

case class InputGreaterThan(x: Exp) extends Exp

case class InputGreaterOrEqual(x: Exp) extends Exp

case class InputEqualTo(x: Exp) extends Exp

case class Interval(start: IntervalBoundary, end: IntervalBoundary) extends Exp

case class AtLeastOne(xs: List[Exp]) extends Exp

case class Not(x: Exp) extends Exp

case class Ref(name: String) extends Exp

case class Addition(x: Exp, y: Exp) extends Exp

case class Subtraction(x: Exp, y: Exp) extends Exp

case class Multiplication(x: Exp, y: Exp) extends Exp

case class Division(x: Exp, y: Exp) extends Exp

case class Exponentiation(x: Exp, y: Exp) extends Exp

case class ArithmeticNegation(x: Exp) extends Exp

case class Equal(x: Exp, y: Exp) extends Exp

case class LessThan(x: Exp, y: Exp) extends Exp

case class LessOrEqual(x: Exp, y: Exp) extends Exp

case class GreaterThan(x: Exp, y: Exp) extends Exp

case class GreaterOrEqual(x: Exp, y: Exp) extends Exp

case class FunctionInvocation(function: String, params: List[Exp]) extends Exp
