package org.camunda.feel.syntaxtree

sealed trait RangeBoundary {
  def value: Exp
}

case class OpenRangeBoundary(value: Exp) extends RangeBoundary

case class ClosedRangeBoundary(value: Exp) extends RangeBoundary
