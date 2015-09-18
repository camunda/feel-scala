package org.camunda.feel.parser

/**
 * @author Philipp Ossler
 */
sealed trait Exp

case class ConstNumber(x: Long) extends Exp

case class ConstBool(b: Boolean) extends Exp

case class ConstString(s: String) extends Exp

case class LessThan(x: Exp) extends Exp