package org.camunda.feel.interpreter

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
case class ValNumber(x: Double) extends Val

case class ValBoolean(b: Boolean) extends Val

case class ValError(error: String) extends Val
