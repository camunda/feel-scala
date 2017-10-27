package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.spi._

import scala.annotation.tailrec
import scala.math.BigDecimal.RoundingMode
import java.time._
import java.time.temporal.ChronoUnit
import java.util.regex._
import org.slf4j._

/**
 * @author Philipp
 */
object BuiltinFunctions extends FunctionProvider {

  val logger = LoggerFactory.getLogger("org.camunda.feel.functions")

  def getFunction(name: String): List[ValFunction] = functions.getOrElse(name, List.empty)

  val functions: Map[String, List[ValFunction]] =
    conversionFunctions ++
    booleanFunctions ++
    stringFunctions ++
    listFunctions ++
    numericFunctions

	private def conversionFunctions = Map(
	  "date" -> List(dateFunction, dateFunction3),
		"date and time" -> List(dateTime, dateTime2),
		"time" -> List(timeFunction, timeFunction3, timeFunction4),
		"number" -> List(numberFunction, numberFunction2, numberFunction3),
		"string" -> List(stringFunction),
		"duration" -> List(durationFunction),
		"years and months duration" -> List(durationFunction2)
	)

	private def booleanFunctions = Map(
	  "not" -> List(notFunction)
	)

	private def stringFunctions = Map(
	  "substring" -> List(substringFunction, substringFunction3),
	  "string length" -> List(stringLengthFunction),
	  "upper case" -> List(upperCaseFunction),
	  "lower case" -> List(lowerCaseFunction),
	  "substring before" -> List(substringBeforeFunction),
	  "substring after" -> List(substringAfterFunction),
	  "replace" -> List(replaceFunction, replaceFunction4),
	  "contains" -> List(containsFunction),
	  "starts with" -> List(startsWithFunction),
	  "ends with" -> List(endsWithFunction),
	  "matches" -> List(matchesFunction, matchesFunction3)
	)

	private def listFunctions = Map(
	  "list contains" -> List(listContainsFunction),
	  "count" -> List(countFunction),
	  "min" -> List(minFunction),
	  "max" -> List(maxFunction),
	  "sum" -> List(sumFunction),
	  "mean" -> List(meanFunction),
	  "and" -> List(andFunction),
	  "or" -> List(orFunction),
	  "sublist" -> List(sublistFunction, sublistFunction3),
	  "append" -> List(appendFunction),
	  "concatenate" -> List(concatenateFunction),
	  "insert before" -> List(insertBeforeFunction),
	  "remove" -> List(removeFunction),
	  "reverse" -> List(reverseFunction),
	  "index of" -> List(indexOfFunction),
	  "union" -> List(unionFunction),
	  "distinct values" -> List(distinctValuesFunction),
	  "flatten" -> List(flattenFunction),
	  "sort" -> List(sortFunction)
	)

	private def numericFunctions = Map(
	  "decimal" -> List(decimalFunction),
	  "floor" -> List(floorFunction),
	  "ceiling" -> List(ceilingFunction)
	)

	private def error(e: List[Val]): Val = e match {
	    case vars if (vars.exists(_.isInstanceOf[ValError])) => vars.filter(_.isInstanceOf[ValError]).head.asInstanceOf[ValError]
	    case e => {
        logger.warn(s"Suppressed failure: illegal arguments: $e")
        ValNull
      }
  }

	def dateFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => ValDate(from)
		case List(ValLocalDateTime(from)) => ValDate(from.toLocalDate())
    case List(ValDateTime(from)) => ValDate(from.toLocalDate())
		case e => error(e)
	})

	def dateFunction3 = ValFunction(List("year", "month", "day"), _ match {
		case List(ValNumber(year), ValNumber(month), ValNumber(day)) => ValDate(LocalDate.of(year.intValue(), month.intValue(), day.intValue()))
		case e => error(e)
	})

	def dateTime = ValFunction(List("from"), _ match {
		case List(ValString(from)) => if(isOffsetDateTime(from)) ValDateTime(from) else ValLocalDateTime(from)
		case e => error(e)
	})

	def dateTime2 = ValFunction(List("date", "time"), _ match {
		case List(ValDate(date), ValLocalTime(time)) => ValLocalDateTime(date.atTime(time))
    case List(ValDate(date), ValTime(time)) => ValDateTime(date.atTime(time))
		case List(ValLocalDateTime(dateTime), ValLocalTime(time)) => ValLocalDateTime(dateTime.toLocalDate().atTime(time))
    case List(ValLocalDateTime(dateTime), ValTime(time)) => ValDateTime(dateTime.toLocalDate().atTime(time))
    case List(ValDateTime(dateTime), ValLocalTime(time)) => ValLocalDateTime(dateTime.toLocalDate().atTime(time))
    case List(ValDateTime(dateTime), ValTime(time)) => ValDateTime(dateTime.toLocalDate().atTime(time))
		case e => error(e)
	})

	def timeFunction = ValFunction(List("from"), _ match {
		case List(ValString(from)) => if (isOffsetTime(from)) ValTime(from) else ValLocalTime(from)
		case List(ValLocalDateTime(from)) => ValLocalTime(from.toLocalTime())
    case List(ValDateTime(from)) => ValTime(from.toOffsetTime())
		case e => error(e)
	})

	def timeFunction3 = ValFunction(List("hour", "minute", "second"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) => ValLocalTime(LocalTime.of(hour.intValue(), minute.intValue(), second.intValue()))
		case e => error(e)
	})

	def timeFunction4 = ValFunction(List("hour", "minute", "second", "offset"), _ match {
		case List(ValNumber(hour), ValNumber(minute), ValNumber(second), ValDayTimeDuration(offset)) => {
      val localTime = LocalTime.of(hour.intValue(), minute.intValue(), second.intValue())
      val zonedOffset = ZoneOffset.ofTotalSeconds(offset.getSeconds.toInt)

      ValTime(localTime.atOffset(zonedOffset))
    }
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
		case List(ValLocalTime(from)) => ValString(from.format(localTimeFormatter))
    case List(ValTime(from)) => ValString(from.format(timeFormatter))
		case List(ValLocalDateTime(from)) => ValString(from.format(localDateTimeFormatter))
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
    case List(ValLocalDateTime(from), ValLocalDateTime(to)) => ValYearMonthDuration( Period.between(from.toLocalDate, to.toLocalDate).withDays(0) )
    case List(ValDateTime(from), ValDateTime(to)) => ValYearMonthDuration( Period.between(from.toLocalDate, to.toLocalDate).withDays(0) )
		case e => error(e)
	})

	def notFunction = ValFunction(List("negand"), _ match {
	  case List(ValBoolean(negand)) => ValBoolean(!negand)
    case List(other: Val) => ValNull
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
	    if (index >= 0) {
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

  def replaceFunction4 = ValFunction(List("input", "pattern", "replacement", "flags"), _ match {
	  case List(ValString(input), ValString(pattern), ValString(replacement), ValString(flags)) => {
      val p = Pattern.compile(pattern, patternFlags(flags))
      val m = p.matcher(input)
      ValString(m.replaceAll(replacement))
    }
	  case e => error(e)
	})

  private def patternFlags(flags: String): Int = {
    var f = 0

    if (flags.contains("s")) {
      f |= Pattern.DOTALL
    }
    if (flags.contains("m")) {
      f |= Pattern.MULTILINE
    }
    if (flags.contains("i")) {
      f |= Pattern.CASE_INSENSITIVE
    }
    if (flags.contains("x")) {
      f |= Pattern.COMMENTS
    }

    f
  }

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

  def matchesFunction3 = ValFunction(List("input", "pattern", "flags"), _ match {
	  case List(ValString(input), ValString(pattern), ValString(flags)) => {
      val p = Pattern.compile(pattern, patternFlags(flags))
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
	}, hasVarArgs = true)

	def maxFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValNull
	    case x :: xs => x match {
	      case ValNumber(n) => withListOfNumbers(list, numbers => ValNumber( numbers.max ))
	      case e => ValError(s"expected number but found '$e")
	    }
	  }
	  case e => error(e)
	}, hasVarArgs = true)

	def sumFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => withListOfNumbers(list, numbers => ValNumber( numbers.sum ))
	  case e => error(e)
	}, hasVarArgs = true)

	def meanFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => list match {
	    case Nil => ValNull
	    case l => withListOfNumbers(list, numbers => ValNumber( numbers.sum / numbers.size ))
	  }
	  case e => error(e)
	}, hasVarArgs = true)

	def andFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => all(list)
	  case e => error(e)
	}, hasVarArgs = true)

  private def all(xs: List[Val]): Val = xs match {
    case Nil => ValBoolean(true)
    case x :: xs => x match {
      case ValBoolean(false) => ValBoolean(false)
      case ValBoolean(true)  => all(xs)
      case other => all(xs) match {
        case ValBoolean(false) => ValBoolean(false)
        case _ => ValNull
      }
    }
  }

	def orFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => atLeastOne(list)
	  case e => error(e)
	}, hasVarArgs = true)

  private def atLeastOne(xs: List[Val]): Val = xs match {
    case Nil => ValBoolean(false)
    case x :: xs => x match {
      case ValBoolean(true) => ValBoolean(true)
      case ValBoolean(false)  => atLeastOne(xs)
      case other => atLeastOne(xs) match {
        case ValBoolean(true) => ValBoolean(true)
        case _ => ValNull
      }
    }
  }

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

  def appendFunction = ValFunction(List("list", "items"), _ match {
	  case List(ValList(list), ValList(items)) => ValList(list ++ items)
	  case e => error(e)
	}, hasVarArgs = true)

	def concatenateFunction = ValFunction(List("lists"), _ match {
	  case List(ValList(lists)) => ValList( lists.flatMap(_ match {
          case ValList(list) => list
          case v => List(v)
      }).toList
    )
	  case e => error(e)
	}, hasVarArgs = true)

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

  def unionFunction = ValFunction(List("lists"), _ match {
    case List(ValList(lists)) => ValList( lists.flatMap(_ match {
          case ValList(list) => list
          case v => List(v)
      }).toList.distinct
    )
	  case e => error(e)
	}, hasVarArgs = true)

	def distinctValuesFunction = ValFunction(List("list"), _ match {
	  case List(ValList(list)) => ValList(list.distinct)
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
	  case List(ValList(list), ValFunction(params, f, _, _)) if (params.size == 2) => try {
	    ValList( list.sortWith{ case (x,y) => f(List(x,y)).asInstanceOf[ValBoolean].value })
	  } catch {
	    case e: Throwable => ValError(s"fail to sort list by given precedes function: $e")
	  }
	  case List(ValList(list), ValFunction(params, _, _, _)) => ValError(s"expect boolean function with 2 arguments, but found '${params.size}'")
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

}
