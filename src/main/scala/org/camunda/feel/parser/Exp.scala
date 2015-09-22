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

case class LessThan(x: Exp) extends Exp

case class LessOrEqual(x: Exp) extends Exp

case class GreaterThan(x: Exp) extends Exp

case class GreaterOrEqual(x: Exp) extends Exp

case class Equal(x: Exp) extends Exp

case class Interval(start: IntervalBoundary, end: IntervalBoundary) extends Exp
