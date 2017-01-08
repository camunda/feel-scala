package org.camunda

import scala.math.BigDecimal
import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField._

/**
 * @author Philipp Ossler
 */
package object feel {
  
  type Number = BigDecimal
  
  type Date = java.time.LocalDate
  
  type Time = java.time.LocalTime
  
  type DateTime = java.time.LocalDateTime
  
  type Duration = javax.xml.datatype.Duration
 
  implicit def stringToNumber(number: String): Number = BigDecimal(number)
  
  implicit def stringToDate(date: String): Date = LocalDate.parse(date)
  
  implicit def stringToTime(time: String): Time = LocalTime.parse(time, timeFormatterWithOptionalPrefix)
  
  implicit def stringToDateTime(dateTime: String): DateTime = LocalDateTime.parse(dateTime)
  
  implicit def stringToDuration(duration: String): Duration = DatatypeFactory.newInstance().newDuration(duration)
  
  def yearMonthDuration(year: Int, month: Int): Duration = DatatypeFactory.newInstance().newDurationYearMonth(true, year, month);
  
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
	val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") 
	
	val timeFormatterWithOptionalPrefix = new DateTimeFormatterBuilder()
  							.optionalStart()
                .appendLiteral('T')
                .optionalEnd()
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .toFormatter();
	  
}