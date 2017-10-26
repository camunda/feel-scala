package org.camunda

import scala.math.BigDecimal
import java.time._
import java.time.format._
import java.time.temporal.ChronoField._

/**
 * @author Philipp Ossler
 */
package object feel {

  type Number = BigDecimal

  type Date = java.time.LocalDate

  type LocalTime = java.time.LocalTime

  type LocalDateTime = java.time.LocalDateTime

  type Time = java.time.OffsetTime

  type DateTime = java.time.OffsetDateTime

  type YearMonthDuration = java.time.Period

  type DayTimeDuration = java.time.Duration

  import scala.language.implicitConversions

  implicit def stringToNumber(number: String): Number = BigDecimal(number)

  implicit def stringToDate(date: String): Date = LocalDate.parse(date)

  implicit def stringToLocalTime(time: String): LocalTime = LocalTime.parse(time, timeFormatterWithOptionalPrefix)

  implicit def stringToTime(time: String): Time = OffsetTime.parse(time, timeFormatterWithOffsetAndOptionalPrefix)

  implicit def stringToLocalDateTime(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime)

  implicit def stringToDateTime(dateTime: String): DateTime = OffsetDateTime.parse(dateTime)

  implicit def stringToYearMonthDuration(duration: String): YearMonthDuration = Period.parse(duration)

  implicit def stringToDayTimeDuration(duration: String): DayTimeDuration = Duration.parse(duration)

  def isOffsetTime(time: String): Boolean = time matches("""T?\d{2}:\d{2}:\d{2}[+-]\d{2}:\d{2}""")

  def isOffsetDateTime(dateTime: String): Boolean = dateTime matches("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}[+-]\d{2}:\d{2}""")

  def isYearMonthDuration(duration: String): Boolean = duration matches("""P(\d+Y)?(\d+M)?""")

  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	val localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx")
	val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
  val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ssxxx")

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

  val timeFormatterWithOffsetAndOptionalPrefix = new DateTimeFormatterBuilder()
  							.append(timeFormatterWithOptionalPrefix)
                .appendOffsetId()
                .toFormatter();

}
