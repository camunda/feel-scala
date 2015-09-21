package org.camunda.feel.parser

import org.camunda.feel.types.Number

/**
 * @author Philipp Ossler
 */
sealed trait Exp

case class ConstNumber(x: Double) extends Exp

case class ConstBool(b: Boolean) extends Exp

case class ConstString(s: String) extends Exp

case class LessThan(x: Exp) extends Exp

case class LessOrEqual(x: Exp) extends Exp

case class GreaterThat(x: Exp) extends Exp

case class GreaterOrEqual(x: Exp) extends Exp

case class Equal(x: Exp) extends Exp