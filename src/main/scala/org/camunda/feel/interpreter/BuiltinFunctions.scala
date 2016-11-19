package org.camunda.feel.interpreter

import org.camunda.feel._
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import javax.xml.datatype.Duration

/**
 * @author Philipp
 */
object BuiltinFunctions {
  
	// conversion functions
	
  // note that some function names has whitespaces in spec 
  // this will be changed in further version
	val builtinFunctions: List[(String, ValFunction)] = List (
		"date" -> dateFunction,
		"date" -> dateFunction3,
		"date_and_time" -> dateTime,	
		"date_and_time" -> dateTime2,
		"time" -> timeFunction,
		"time" -> timeFunction3,
		"time" -> timeFunction4,
		"string" -> stringFunction,
		"duration" -> durationFunction,
		"years_and_months_duration" -> durationFunction2
	)
	
	def dateFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDate(from)
		case List(ValDateTime(from)) => ValDate(from.toLocalDate())
		case e => error(e)
	})
	
	def dateFunction3 = ValFunction(List("year", "month", "day"), _ match {
		case List(ValNumber(year), ValNumber(month), ValNumber(day)) => ValDate(new LocalDate(year.intValue(), month.intValue(), day.intValue()))
		case e => error(e)
	})
	
	def dateTime = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDateTime(from)
		case e => error(e)
	})
	
	def dateTime2 = ValFunction(List("date", "time"), _ match {
		case List(ValDate(date), ValTime(time)) => ValDateTime(date.toLocalDateTime(time))
		case List(ValDateTime(dateTime), ValTime(time)) => ValDateTime(dateTime.toLocalDate().toLocalDateTime(time))
		case e => error(e)
	})	
	
	def timeFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValTime(from)
		case List(ValDateTime(from)) => ValTime(from.toLocalTime())
		case e => error(e)
	})
	
	def timeFunction3 = ValFunction(List("hour", "minute", "second"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) => ValTime(new LocalTime(hour.intValue(), minute.intValue(), second.intValue()))
		case e => error(e)
	})
	
	def timeFunction4 = ValFunction(List("hour", "minute", "second", "offset"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second), ValDuration(offset)) => ValTime(new LocalTime(hour.intValue() + offset.getHours, minute.intValue() + offset.getMinutes, second.intValue() + offset.getSeconds))
		case e => error(e)
	})
	
	def stringFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValString(from)
		case List(ValBoolean(from)) => ValString(from.toString)
		case List(ValNumber(from)) => ValString(from.toString)
		case List(ValDate(from)) => ValString(from.toString("yyyy-MM-dd"))
		case List(ValTime(from)) => ValString(from.toString("HH:mm:ss"))
		case List(ValDateTime(from)) => ValString(from.toString("yyyy-MM-dd'T'HH:mm:ss"))
		case List(ValDuration(from)) => ValString(from.toString)
		case e => error(e)
	})
	
	def durationFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDuration(from)
		case e => error(e)
	})
	
	def durationFunction2 = ValFunction(List("from", "to"), _ match {
	  case List(ValDate(from), ValDate(to)) => {
		  val year = to.getYear - from.getYear
		  val month = to.getMonthOfYear - from.getMonthOfYear
		 
		  val duration = if (month >= 0) {
		    yearMonthDuration(year, month)
		  } else {
		    yearMonthDuration(year - 1, month + 12)
		  }
		  
		  ValDuration(duration)
		}
		case e => error(e)
	})
	
	private def error(e:List[Val]) = ValError(s"illegal arguments: $e")
	
}