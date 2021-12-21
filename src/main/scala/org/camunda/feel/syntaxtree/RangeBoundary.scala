package org.camunda.feel.syntaxtree

sealed trait RangeBoundary {
  def value: Val
  def isClosed: Boolean
}

case class OpenRangeBoundary(value: Val) extends RangeBoundary {
  override def isClosed: Boolean = false
}

case class ClosedRangeBoundary(value: Val) extends RangeBoundary {
  override def isClosed: Boolean = true
}
