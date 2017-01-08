package org.camunda.feel

import org.camunda.feel._
import org.camunda.feel.interpreter._
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalTime
import java.time.LocalDateTime

object ValueMapper {
  
  def toVal(x: Any): Val = x match {
    case null => ValNull
    case x: Int => ValNumber(x)
    case x: Long => ValNumber(x)
    case x: Float => ValNumber(x)
    case x: Double => ValNumber(x)
    case x: Boolean => ValBoolean(x)
    case x: String => ValString(x)
    case x: Date => ValDate(x)
    case x: Time => ValTime(x)
    case x: DateTime => ValDateTime(x)
    case x: Duration => ValDuration(x)
    case x: List[_] => ValList( x map toVal )
    case x: Map[_,_] => ValContext( x map { case (key, value) => key.toString -> toVal(value)} toList)
    // extended java types
    case x: java.util.Date => ValDateTime(x.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
    // yoda-time
    case x: org.joda.time.LocalDate => ValDate(LocalDate.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth))
    case x: org.joda.time.LocalTime => ValTime(LocalTime.of(x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    case x: org.joda.time.LocalDateTime => ValDateTime(LocalDateTime.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth, x.getHourOfDay, x.getMinuteOfHour, x.getSecondOfMinute))
    // unsupported values
    case _ => ValError(s"unsupported type '$x'")
  }
  
  def unpackVal(value: Val): Any = value match {
    case ValNull => null
    case ValBoolean(boolean) => boolean
    case ValNumber(number) => number
    case ValString(string) => string
    case ValDate(date) => date
    case ValTime(time) => time
    case ValDateTime(dateTime) => dateTime
    case ValDuration(duration) => duration
    case ValList(list) => list map unpackVal
    case ValContext(context) => context map { case (key, value) => key -> unpackVal(value) } toMap
    case ValError(error) => new Exception(error)
    case _ => throw new IllegalArgumentException(s"unexpected val '$value'")
  }
  
}