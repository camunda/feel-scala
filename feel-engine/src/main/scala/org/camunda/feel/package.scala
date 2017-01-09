package org.camunda

import scala.math.BigDecimal
import javax.xml.datatype.DatatypeFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField._
import java.time.Duration
import java.time.Period

/**
 * @author Philipp Ossler
 */
package object feel {
  
  type Number = BigDecimal
  
  type Date = java.time.LocalDate
  
  type Time = java.time.LocalTime
  
  type DateTime = java.time.LocalDateTime
  
  type YearMonthDuration = java.time.Period
  
  type DayTimeDuration = java.time.Duration
  
  implicit def stringToNumber(number: String): Number = BigDecimal(number)
  
  implicit def stringToDate(date: String): Date = LocalDate.parse(date)
  
  implicit def stringToTime(time: String): Time = LocalTime.parse(time, timeFormatterWithOptionalPrefix)
  
  implicit def stringToDateTime(dateTime: String): DateTime = LocalDateTime.parse(dateTime)
  
  implicit def stringToYearMonthDuration(duration: String): YearMonthDuration = Period.parse(duration)
  
  implicit def stringToDayTimeDuration(duration: String): DayTimeDuration = Duration.parse(duration)
  
  def isYearMonthDuration(duration: String): Boolean = duration matches("""P(\d+Y)?(\d+M)?""")
  
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