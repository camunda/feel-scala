package org.camunda.feel.syntaxtree

sealed trait RangeBoundary {
  def value: ValNumber
  def isClosed: Boolean
}

case class OpenRangeBoundary(value: ValNumber) extends RangeBoundary {
  override def isClosed: Boolean = false
}

case class ClosedRangeBoundary(value: ValNumber) extends RangeBoundary {
  override def isClosed: Boolean = true
}
