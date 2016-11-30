package org.camunda.feel.interpreter

import org.camunda.feel._
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import javax.xml.datatype.Duration

/**
 * @author Philipp
 */
object BuiltinFunctions {
  
  // note that some function names has whitespaces in spec 
  // this will be changed in further version
	
  val builtinFunctions: List[(String, ValFunction)] = 
    conversionFunctions ++
    booleanFunctions ++
    stringFunctions ++
    listFunctions
	
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
	  "distinct_values" -> distinctValuesFunction
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
	
	def notFunction = ValFunction(List("negand"), _ match {
	  case List(ValBoolean(negand)) => ValBoolean(!negand)
	  case e => error(e)
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
	  case List(ValString(input), ValString(pattern)) => ValBoolean(input.matches(pattern))
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
	  case List(ValList(list), ValNumber(start), ValNumber(length)) => ValList(list.slice(listIndex(list, start.intValue), length.intValue)) 
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
	
	private def indexOfList(list: List[Val], item: Val, from: Int = 0): List[Int] = {
	  val index = list.indexOf(item, from)
	  
	  if (index > 0) {
	    (index + 1) :: indexOfList(list, item, index + 1)  
	  } else {
	    Nil  
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
	
	private def error(e:List[Val]) = ValError(s"illegal arguments: $e")
	
}