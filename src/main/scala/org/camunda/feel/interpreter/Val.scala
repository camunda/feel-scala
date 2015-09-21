package org.camunda.feel.interpreter

import org.joda.time.LocalDate

/**
 * FEEL supports the following datatypes:
* number
* string
* boolean
* days and time duration
* years and months duration
* time
* date and time
Duration and date/time datatypes have no literal syntax. They must be constructed from a string representation using a
built-in function (10.3.4.1).
 * 
 * @author Philipp Ossler
 */
sealed trait Val

// should be BigDecimal (or simple Double)
case class ValNumber(value: Double) extends Val

case class ValBoolean(value: Boolean) extends Val

case class ValDate(value: LocalDate) extends Val

case class ValError(error: String) extends Val
