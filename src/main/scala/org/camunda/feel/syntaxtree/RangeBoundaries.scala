package org.camunda.feel.syntaxtree

import org.camunda.feel._
sealed trait RangeBoundaries {
  def start: Number
  def end: Number
  def startIncl: ClosedBoundary
  def endIncl: ClosedBoundary
}

case class RangeWithBoundaries(start: Number,
                               end: Number,
                               startIncl: ClosedBoundary,
                               endIncl: ClosedBoundary)
    extends RangeBoundaries
