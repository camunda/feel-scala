package org.camunda.feel.syntaxtree

sealed trait ConstRangeBoundary {
  def value: Exp
}

case class OpenConstRangeBoundary(value: Exp) extends ConstRangeBoundary

case class ClosedConstRangeBoundary(value: Exp) extends ConstRangeBoundary
