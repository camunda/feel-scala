package org.camunda

import org.joda.time.LocalDate
import scala.math.BigDecimal

/**
 * @author Philipp Ossler
 */
package object feel {
  
  type Number = BigDecimal
  
  type Date = LocalDate
 
  implicit def stringToNumber(number: String): Number = BigDecimal(number)
  
  implicit def stringToDate(date: String): Date = LocalDate.parse(date)
  
}