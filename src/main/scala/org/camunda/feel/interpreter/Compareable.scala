package org.camunda.feel.interpreter

import org.joda.time.LocalDate


/**
 * @author Philipp Ossler
 */
trait Compareable[T] {
  
  def x: T

  def <(y: Compareable[_]): Boolean = op(y, <)
  
  def <=(y: Compareable[_]): Boolean = op(y, <=)
  
  def >(y: Compareable[_]): Boolean = op(y, >)
  
  def >=(y: Compareable[_]): Boolean = op(y, >=)
  
  def <(y: T): Boolean
  
  def <=(y: T): Boolean
  
  def >(y: T): Boolean
  
  def >=(y: T): Boolean
  
  private def op(y: Compareable[_], f: T => Boolean): Boolean = y match {
    case i :Compareable[T] => f(i.x)
    case i => throw new IllegalArgumentException(s"expect '${x.getClass}' but found '${y.getClass}'")
  }
}

object Compareable {

  implicit def numberToCompareable(x: Double): Compareable[Double] = CompareableNumber(x)

  implicit def dateToCompareable(x: LocalDate): Compareable[LocalDate] = CompareableDate(x)
}

case class CompareableNumber(x: Double) extends Compareable[Double] {
  
  def <(y: Double) = x < y
  
  def <=(y: Double) = x <= y
  
  def >(y: Double) = x > y
  
  def >=(y: Double) = x >= y
  
}

case class CompareableDate(x: LocalDate) extends Compareable[LocalDate] {
  
  def <(y: LocalDate) = x isBefore y
  
  def <=(y: LocalDate) = x == y || (x isBefore y)
  
  def >(y: LocalDate) = x isAfter y
  
  def >=(y: LocalDate) = x == y || (x isAfter y)
  
}