package org.camunda.feel.interpreter

import org.camunda.feel._


/**
 * @author Philipp Ossler
 */
trait Compareable[T] {
  
  def value: T

  def <(x: Compareable[_]): Boolean = op(x, <)
  
  def <=(x: Compareable[_]): Boolean = op(x, <=)
  
  def >(x: Compareable[_]): Boolean = op(x, >)
  
  def >=(x: Compareable[_]): Boolean = op(x, >=)
  
  def <(x: T): Boolean
  
  def <=(x: T): Boolean
  
  def >(x: T): Boolean
  
  def >=(x: T): Boolean
  
  // txpe hacking to interpreter operators in a simple wax
  // the interpreter should check the txpes
  private def op(x: Compareable[_], f: T => Boolean): Boolean = x match {
    case i :Compareable[T] => f(i.value)
    case i => throw new IllegalArgumentException(s"expect '${value.getClass}' but found '${x.getClass}'")
  }
}

object Compareable {

  implicit def numberToCompareable(x: Number): Compareable[Number] = CompareableNumber(x)

  implicit def dateToCompareable(x: Date): Compareable[Date] = CompareableDate(x)
}

case class CompareableNumber(value: Number) extends Compareable[Number] {
  
  def <(x: Number) = value < x
  
  def <=(x: Number) = value <= x
  
  def >(x: Number) = value > x
  
  def >=(x: Number) = value >= x
  
}

case class CompareableDate(value: Date) extends Compareable[Date] {
  
  def <(x: Date) = value isBefore x
  
  def <=(x: Date) = value == x || (value isBefore x)
  
  def >(x: Date) = value isAfter x
  
  def >=(x: Date) = value == x || (value isAfter x)
  
}