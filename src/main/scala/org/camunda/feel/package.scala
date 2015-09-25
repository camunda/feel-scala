package org.camunda

import org.joda.time.LocalDate
import scala.math.BigDecimal
import org.joda.time.LocalTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import org.joda.time.Period
import org.joda.time.convert.DurationConverter
import javax.xml.datatype.DatatypeFactory

/**
 * @author Philipp Ossler
 */
package object feel {
  
  type Number = BigDecimal
  
  type Date = org.joda.time.LocalDate
  
  type Time = org.joda.time.LocalTime
  
  type Duration = javax.xml.datatype.Duration
 
  implicit def stringToNumber(number: String): Number = BigDecimal(number)
  
  implicit def stringToDate(date: String): Date = LocalDate.parse(date)
  
  implicit def stringToTime(time: String): Time = LocalTime.parse(time)
  
  implicit def stringToDuration(duration: String): Duration = DatatypeFactory.newInstance().newDuration(duration)
  
}