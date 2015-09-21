package org.camunda.feel.types

import org.camunda.feel.parser.Exp

/**
 * @author Philipp Ossler
 */

sealed trait IntervalBoundary {
  def value: Exp
}

case class OpenIntervalBoundary(value: Exp) extends IntervalBoundary

case class ClosedIntervalBoundary(value: Exp) extends IntervalBoundary
