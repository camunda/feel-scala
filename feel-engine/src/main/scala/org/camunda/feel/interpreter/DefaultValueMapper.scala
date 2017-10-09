package org.camunda.feel.interpreter

import org.camunda.feel._
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.Duration
import java.time.Period

import scala.collection.JavaConverters._

class DefaultValueMapper extends ValueMapper {

  def toVal(x: Any): Val = x match {

    case x: Val => x
    case null => ValNull

    // scala types
    case x: Int => ValNumber(x)
    case x: Long => ValNumber(x)
    case x: Float if (x.isNaN || x.isInfinity) => ValNull
    case x: Float => ValNumber(x)
    case x: Double if (x.isNaN || x.isInfinity) => ValNull
    case x: Double => ValNumber(x)
    case x: BigDecimal => ValNumber(x)
    case x: Boolean => ValBoolean(x)
    case x: String => ValString(x)
    case x: Date => ValDate(x)
    case x: Time => ValTime(x)
    case x: DateTime => ValDateTime(x)
    case x: YearMonthDuration => ValYearMonthDuration(x)
    case x: DayTimeDuration => ValDayTimeDuration(x)
    case x: List[_] => ValList( x map toVal )
    case x: Map[_,_] => ValContext(DefaultContext(x map { case (key, value) => key.toString -> toVal(value)}))
    case Some(x) => toVal(x)
    case None => ValNull
    case x: Enumeration$Val => ValString(x.toString)

    // extended java types
    case x: java.math.BigDecimal => ValNumber(x)
    case x: java.util.Date => ValDateTime(x.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
    case x: java.util.List[_] => ValList( x.asScala.toList map toVal )
    case x: java.util.Map[_,_] => ValContext(DefaultContext(x.asScala map { case (key, value) => key.toString -> toVal(value)} toMap))
    case x: java.lang.Enum[_] => ValString(x.name)

    // joda-time
    case x: org.joda.time.LocalDate => ValDate(LocalDate.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth))
    case x: org.joda.time.LocalTime => ValTime(LocalTime.of(x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    case x: org.joda.time.LocalDateTime => ValDateTime(LocalDateTime.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth, x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    case x: org.joda.time.Duration => ValDayTimeDuration( Duration.ofMillis( x.getMillis ) )
    case x: org.joda.time.Period => ValYearMonthDuration( Period.of(x.getYears, x.getMonths, 0) )

    // other objects
    case x: Throwable => ValError(x.getMessage)
    case x => try {
      ValContext(ObjectContext(x, this))
    } catch {
      case _: Throwable => ValError(s"unsupported object '$x' of class '${x.getClass}'")
    }

  }

  def unpackVal(value: Val): Any = value match {
    case ValNull => null
    case ValBoolean(boolean) => boolean
    case ValNumber(number) => number
    case ValString(string) => string
    case ValDate(date) => date
    case ValTime(time) => time
    case ValDateTime(dateTime) => dateTime
    case ValYearMonthDuration(duration) => duration
    case ValDayTimeDuration(duration) => duration
    case ValList(list) => list map unpackVal
    case ValContext(dc: DefaultContext) => dc.variables map { case (key, value) => key -> unpackVal(toVal(value)) } toMap
    case ValError(error) => new Exception(error)
    case _ => throw new IllegalArgumentException(s"unexpected val '$value'")
  }

}

object DefaultValueMapper {

  val instance = new DefaultValueMapper

}
