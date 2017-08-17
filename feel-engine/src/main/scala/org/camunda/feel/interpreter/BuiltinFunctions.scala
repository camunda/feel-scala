package org.camunda.feel.interpreter

import org.camunda.feel._
import scala.annotation.tailrec
import scala.math.BigDecimal.RoundingMode
import java.math.MathContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Period
import java.util.regex._
import org.camunda.feel.spi.FunctionProvider

/**
 * @author Philipp
 */
object BuiltinFunctions extends FunctionProvider {

  // note that some function names has whitespaces in spec
  // this will be changed in further version

  val builtinFunctions: List[(String, ValFunction)] =
    conversionFunctions ++
    booleanFunctions ++
    stringFunctions ++
    listFunctions ++
    numericFunctions

	def conversionFunctions = List(
	  "date" -> dateFunction,
		"date" -> dateFunction3,
		"date_and_time" -> dateTime,
		"date_and_time" -> dateTime2,
		"time" -> timeFunction,
		"time" -> timeFunction3,
		"time" -> timeFunction4,
		"number" -> numberFunction,
		"number" -> numberFunction2,
		"number" -> numberFunction3,
		"string" -> stringFunction,
		"duration" -> durationFunction,
		"years_and_months_duration" -> durationFunction2
	)

	def booleanFunctions = List(
	  "not" -> notFunction
	)

	def stringFunctions = List(
	  "substring" -> substringFunction,
	  "substring" -> substringFunction3,
	  "string_length" -> stringLengthFunction,
	  "upper_case" -> upperCaseFunction,
	  "lower_case" -> lowerCaseFunction,
	  "substring_before" -> substringBeforeFunction,
	  "substring_after" -> substringAfterFunction,
	  "replace" -> replaceFunction,
	  "contains" -> containsFunction,
	  "starts_with" -> startsWithFunction,
	  "ends_with" -> endsWithFunction,
	  "matches" -> matchesFunction
	)

	def listFunctions = List(
	  "list_contains" -> listContainsFunction,
	  "count" -> countFunction,
	  "min" -> minFunction,
	  "max" -> maxFunction,
	  "sum" -> sumFunction,
	  "mean" -> meanFunction,
	  "and" -> andFunction,
	  "or" -> orFunction,
	  "sublist" -> sublistFunction,
	  "sublist" -> sublistFunction3,
	  "append" -> appendFunction,
	  "concatenate" -> concatenateFunction,
	  "insert_before" -> insertBeforeFunction,
	  "remove" -> removeFunction,
	  "reverse" -> reverseFunction,
	  "index_of" -> indexOfFunction,
	  "union" -> unionFunction,
	  "distinct_values" -> distinctValuesFunction,
	  "flatten" -> flattenFunction,
	  "sort" -> sortFunction
	)

	def numericFunctions = List(
	  "decimal" -> decimalFunction,
	  "floor" -> floorFunction,
	  "ceiling" -> ceilingFunction
	)

	private val builtinFunctionsByNameAndArgCount: Map[(String, Int), ValFunction] = builtinFunctions
		.map { case (name, f) => (name, f.params.size) -> f }
	  .toMap

	def getFunction(name: String, argCount: Int) = builtinFunctionsByNameAndArgCount.get((name, argCount))

	private def error(e: List[Val]): ValError = e match {
	    case vars if (vars.exists(_.isInstanceOf[ValError])) => vars.filter(_.isInstanceOf[ValError]).head.asInstanceOf[ValError]
	    case _ => ValError(s"illegal arguments: $e")
  }

	def dateFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDate(from)
		case List(ValDateTime(from)) => ValDate(from.toLocalDate())
		case e => error(e)
	})

	def dateFunction3 = ValFunction(List("year", "month", "day"), _ match {
		case List(ValNumber(year), ValNumber(month), ValNumber(day)) => ValDate(LocalDate.of(year.intValue(), month.intValue(), day.intValue()))
		case e => error(e)
	})

	def dateTime = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDateTime(from)
		case e => error(e)
	})

	def dateTime2 = ValFunction(List("date", "time"), _ match {
		case List(ValDate(date), ValTime(time)) => ValDateTime(date.atTime(time))
		case List(ValDateTime(dateTime), ValTime(time)) => ValDateTime(dateTime.toLocalDate().atTime(time))
		case e => error(e)
	})

	def timeFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValTime(from)
		case List(ValDateTime(from)) => ValTime(from.toLocalTime())
		case e => error(e)
	})

	def timeFunction3 = ValFunction(List("hour", "minute", "second"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) => ValTime(LocalTime.of(hour.intValue(), minute.intValue(), second.intValue()))
		case e => error(e)
	})

	def timeFunction4 = ValFunction(List("hour", "minute", "second", "offset"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second), ValDayTimeDuration(offset)) => ValTime(LocalTime.of(hour.intValue(), minute.intValue(), second.intValue()).plus(offset))
		case e => error(e)
	})

	def numberFunction = ValFunction(List("from"), _ match {
	  case List(ValString(from)) => ValNumber(from)
	  case e => error(e)
	})

	def numberFunction2 = ValFunction(List("from", "grouping"), _ match {
	  case List(ValString(from), ValString(grouping)) if(isValidGroupingSeparator(grouping)) => ValNumber(from.replace(grouping, ""))
	  case List(ValString(from), ValString(grouping)) => ValError(s"illegal argument for grouping. Must be one of ' ', ',' or '.'")
	  case e => error(e)
	})

	def numberFunction3 = ValFunction(List("from", "grouping", "decimal"), _ match {
	  case List(ValString(from), ValString(grouping), ValString(decimal)) if(isValidGroupingSeparator(grouping) && isValidDecimalSeparator(decimal) && grouping != decimal)
	    => ValNumber(from.replace(grouping, "").replace(decimal, "."))
	  case List(ValString(from), ValString(grouping), ValString(decimal)) => ValError(s"illegal arguments for grouping or decimal. Must be one of ' ' (grouping only), ',' or '.'")
	  case e => error(e)
	})

	private def isValidGroupingSeparator(separator: String) = separator == " " || separator == "," || separator == "."

	private def isValidDecimalSeparator(separator: String) = separator == "," || separator == "."

	def stringFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValString(from)
		case List(ValBoolean(from)) => ValString(from.toString)
		case List(ValNumber(from)) => ValString(from.toString)
		case List(ValDate(from)) => ValString(from.format(dateFormatter))
		case List(ValTime(from)) => ValString(from.format(timeFormatter))
		case List(ValDateTime(from)) => ValString(from.format(dateTimeFormatter))
		case List(ValYearMonthDuration(from)) => ValString(from.toString)
		case List(ValDayTimeDuration(from)) => ValString(from.toString)
		case e => error(e)
	})

	def durationFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => if(isYearMonthDuration(from)) ValYearMonthDuration(from) else ValDayTimeDuration(from)
		case e => error(e)
	})

	def durationFunction2 = ValFunction(List("from", "to"), _ match {
	  case List(ValDate(from), ValDate(to)) => ValYearMonthDuration( Period.between(from, to).withDays(0) )
		case e => error(e)
	})

	def notFunction = ValFunction(List("negand"), _ match {
	  case List(ValBoolean(negand)) => ValBoolean(!negand)
	  case other => ValNull
	})

	def substringFunction = ValFunction(List("string", "start"), _ match {
	  case List(ValString(string), ValNumber(start)) => ValString(string.substring(stringIndex(string, start.intValue())))
	  case e => error(e)
	})

	def substringFunction3 = ValFunction(List("string", "start", "length"), _ match {
	  case List(ValString(string), ValNumber(start), ValNumber(length)) => ValString(string.substring(stringIndex(string, start.intValue()), stringIndex(string, start.intValue()) + length.intValue))
	  case e => error(e)
	})

	private def stringIndex(string: String, index: Int) = if (index > 0) {
	  index - 1
	} else {
	  string.length + index
	}

  def stringLengthFunction = ValFunction(List("string"), _ match {
	  case List(ValString(string)) => ValNumber(string.length)
	  case e => error(e)
	})

	def upperCaseFunction = ValFunction(List("string"), _ match {
	  case List(ValString(string)) => ValString(string.toUpperCase)
	  case e => error(e)
	})

	def lowerCaseFunction = ValFunction(List("string"), _ match {
	  case List(ValString(string)) => ValString(string.toLowerCase)
	  case e => error(e)
	})

	def substringBeforeFunction = ValFunction(List("string", "match"), _ match {
	  case List(ValString(string), ValString(m)) => {
	    val index = string.indexOf(m)
	    if (index > 0) {
	      ValString(string.substring(0, index))
	    } else {
	      ValString("")
	    }
	  }
	  case e => error(e)
	})

	def substringAfterFunction = ValFunction(List("string", "match"), _ match {
	  case List(ValString(string), ValString(m)) => {
	    val index = string.indexOf(m)
	    if (index > 0) {
	      ValString(string.substring(index + m.length))
	    } else {
	      ValString("")
	    }
	  }
	  case e => error(e)
	})

	def replaceFunction = ValFunction(List("input", "pattern", "replacement"), _ match {
	  case List(ValString(input), ValString(pattern), ValString(replacement)) => ValString(input.replaceAll(pattern, replacement))
	  case e => error(e)
	})

	def containsFunction = ValFunction(List("string", "match"), _ match {
	  case List(ValString(string), ValString(m)) => ValBoolean(string.contains(m))
	  case e => error(e)
	})

	def startsWithFunction = ValFunction(List("string", "match"), _ match {
	  case List(ValString(string), ValString(m)) => ValBoolean(string.startsWith(m))
	  case e => error(e)
	})

	def endsWithFunction = ValFunction(List("string", "match"), _ match {
	  case List(ValString(string), ValString(m)) => ValBoolean(string.endsWith(m))
	  case e => error(e)
	})

	def matchesFunction = ValFunction(List("input", "pattern"), _ match {
	  case List(ValString(input), ValString(pattern)) => {
      val p = Pattern.compile(pattern)
      val m = p.matcher(input)
      ValBoolean(m.find)
    }
	  case e => error(e)
	})

	def listContainsFunction = ValFunction(List("list", "element"), _ match {
	  case List(ValList(list), element) => ValBoolean(list.contains(element))
	  case e => error(e)
	})

	def countFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => ValNumber(list.size)
	  case e => error(e)
	})

	def minFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValNull
	    case x :: xs => x match {
	      case ValNumber(n) => withListOfNumbers(list, numbers => ValNumber( numbers.min ))
	      case e => ValError(s"expected number but found '$e")
	    }
	  }
	  case e => error(e)
	})

	def maxFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValNull
	    case x :: xs => x match {
	      case ValNumber(n) => withListOfNumbers(list, numbers => ValNumber( numbers.max ))
	      case e => ValError(s"expected number but found '$e")
	    }
	  }
	  case e => error(e)
	})

	def sumFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => withListOfNumbers(list, numbers => ValNumber( numbers.sum ))
	  case e => error(e)
	})

	def meanFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValNull
	    case l => withListOfNumbers(list, numbers => ValNumber( numbers.sum / numbers.size ))
	  }
	  case e => error(e)
	})

	def andFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValBoolean(true)
	    case l => withListOfBooleans(list, numbers => ValBoolean( numbers.reduce(_ && _) ))
	  }
	  case e => error(e)
	})

	def orFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValBoolean(false)
	    case l => withListOfBooleans(list, numbers => ValBoolean( numbers.reduce(_ || _) ))
	  }
	  case e => error(e)
	})

	def sublistFunction = ValFunction(List("list", "start"), _ match {
	  case List(ValList(list), ValNumber(start)) => ValList(list.slice(listIndex(list, start.intValue), list.length))
	  case e => error(e)
	})

	def sublistFunction3 = ValFunction(List("list", "start", "length"), _ match {
	  case List(ValList(list), ValNumber(start), ValNumber(length)) => ValList(list.slice(listIndex(list, start.intValue), listIndex(list, start.intValue) + length.intValue))
	  case e => error(e)
	})

	private def listIndex(list: List[_], index: Int) = if (index > 0) {
	  index - 1
	} else {
	  list.size + index
	}

  def appendFunction = ValFunction(List("list", "item"), _ match {
	  case List(ValList(list), item: Val) => ValList(list ++ (item :: Nil))
	  case e => error(e)
	})

	def concatenateFunction = ValFunction(List("list", "other"), _ match {
	  case List(ValList(list), ValList(other)) => ValList(list ++ other)
	  case e => error(e)
	})

	def insertBeforeFunction = ValFunction(List("list", "position", "newItem"), _ match {
	  case List(ValList(list), ValNumber(position), newItem: Val) => ValList(list.take(listIndex(list, position.intValue)) ++ (newItem :: Nil) ++ list.drop(listIndex(list, position.intValue)))
	  case e => error(e)
	})

	def removeFunction = ValFunction(List("list", "position"), _ match {
	  case List(ValList(list), ValNumber(position)) => ValList(list.take(listIndex(list, position.intValue)) ++ list.drop(listIndex(list, position.intValue + 1)))
	  case e => error(e)
	})

	def reverseFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => ValList(list.reverse)
	  case e => error(e)
	})

	def indexOfFunction = ValFunction(List("list", "match"), _ match {
	  case List(ValList(list), m: Val) => ValList(indexOfList(list, m) map(ValNumber(_)))
	  case e => error(e)
	})

	@tailrec
	private def indexOfList(list: List[Val], item: Val, from: Int = 0, indexList: List[Int] = List()): List[Int] = {
	  val index = list.indexOf(item, from)

	  if (index > 0) {
	    indexOfList(list, item, index + 1, indexList ++ List(index + 1))
	  } else {
	    indexList
	  }
  }

  def unionFunction = ValFunction(List("list", "other"), _ match {
	  case List(ValList(list), ValList(other)) => ValList((list ++ other) distinct)
	  case e => error(e)
	})

	def distinctValuesFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => ValList(list distinct)
	  case e => error(e)
	})

	def flattenFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => ValList(flatten(list))
	  case e => error(e)
	})

	private def flatten(list: List[Val]): List[Val] = list match {
    case Nil => Nil
    case ValList(l) :: xs => flatten(l) ++ flatten(xs)
    case x :: xs => x :: flatten(xs)
  }

  def sortFunction = ValFunction(List("list", "precedes"), _ match {
	  case List(ValList(list), ValFunction(params, f, _)) if (params.size == 2) => try {
	    ValList( list.sortWith{ case (x,y) => f(List(x,y)).asInstanceOf[ValBoolean].value })
	  } catch {
	    case e: Throwable => ValError(s"fail to sort list by given precedes function: $e")
	  }
	  case List(ValList(list), ValFunction(params, _, _)) => ValError(s"expect boolean function with 2 arguments, but found '${params.size}'")
	  case e => error(e)
	})

  def decimalFunction = ValFunction(List("n", "scale"), _ match {
	  case List(ValNumber(n), ValNumber(scale)) => ValNumber(n.setScale(scale.intValue, RoundingMode.HALF_EVEN))
	  case e => error(e)
	})

	def floorFunction = ValFunction(List("n"), _ match {
	  case List(ValNumber(n)) => ValNumber(n.setScale(0, RoundingMode.FLOOR))
	  case e => error(e)
	})

	def ceilingFunction = ValFunction(List("n"), _ match {
	  case List(ValNumber(n)) => ValNumber(n.setScale(0, RoundingMode.CEILING))
	  case e => error(e)
	})

	private def withListOfNumbers(list: List[Val], f: List[Number] => Val): Val = {
    list
      .map( _ match {
        case n: ValNumber => n
        case x => ValError(s"expected number but found '$x'")
      })
      .find( _.isInstanceOf[ValError]) match {
        case Some(e) => e
        case None => f( list.asInstanceOf[List[ValNumber]].map( _.value ) )
      }
  }

  private def withListOfBooleans(list: List[Val], f: List[Boolean] => Val): Val = {
    list
      .map( _ match {
        case b: ValBoolean => b
        case x => ValError(s"expected boolean but found '$x'")
      })
      .find( _.isInstanceOf[ValError]) match {
        case Some(e) => e
        case None => f( list.asInstanceOf[List[ValBoolean]].map( _.value ) )
      }
  }

}
