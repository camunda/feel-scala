/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.impl.interpreter

import java.math.BigDecimal
import java.time.{LocalDate, LocalTime, Period, ZoneOffset}
import java.util.regex.Pattern

import org.camunda.feel.context.{Context, FunctionProvider}
import org.camunda.feel.syntaxtree._
import org.camunda.feel._

import scala.annotation.tailrec
import scala.math.BigDecimal.RoundingMode
import scala.util.Try

/**
 * @author Philipp
 */
object BuiltinFunctions extends FunctionProvider {

  override def getFunctions(name: String): List[ValFunction] =
    functions.getOrElse(name, List.empty)

  override def functionNames: Iterable[String] = functions.keys

  val functions: Map[String, List[ValFunction]] =
    conversionFunctions ++
      booleanFunctions ++
      stringFunctions ++
      listFunctions ++
      numericFunctions ++
      contextFunctions

  private def conversionFunctions =
    Map(
      "date" -> List(dateFunction, dateFunction3),
      "date and time" -> List(dateTime, dateTime2),
      "time" -> List(timeFunction, timeFunction3, timeFunction4),
      "number" -> List(numberFunction, numberFunction2, numberFunction3),
      "string" -> List(stringFunction),
      "duration" -> List(durationFunction),
      "years and months duration" -> List(durationFunction2)
    )

  private def booleanFunctions = Map("not" -> List(notFunction))

  private def stringFunctions =
    Map(
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
      "matches" -> List(matchesFunction, matchesFunction3),
      "split" -> List(splitFunction)
    )

  private def listFunctions =
    Map(
      "list contains" -> List(listContainsFunction),
      "count" -> List(countFunction),
      "min" -> List(minFunction),
      "max" -> List(maxFunction),
      "sum" -> List(sumFunction),
      "product" -> List(productFunction),
      "mean" -> List(meanFunction),
      "median" -> List(medianFunction),
      "stddev" -> List(stddevFunction),
      "mode" -> List(modeFunction),
      "and" -> List(andFunction),
      "all" -> List(andFunction),
      "or" -> List(orFunction),
      "any" -> List(orFunction),
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

  private def numericFunctions =
    Map(
      "decimal" -> List(decimalFunction, decimalFunction3),
      "floor" -> List(floorFunction),
      "ceiling" -> List(ceilingFunction),
      "abs" -> List(absFunction),
      "modulo" -> List(moduloFunction),
      "sqrt" -> List(sqrtFunction),
      "log" -> List(logFunction),
      "exp" -> List(expFunction),
      "odd" -> List(oddFunction),
      "even" -> List(evenFunction)
    )

  private def contextFunctions =
    Map("get entries" -> List(getEntriesFunction),
      "get value" -> List(getValueFunction))

  private def error(e: List[Val]): Val = e match {
    case vars if (vars.exists(_.isInstanceOf[ValError])) =>
      vars.filter(_.isInstanceOf[ValError]).head.asInstanceOf[ValError]
    case e => {
      logger.warn(s"Suppressed failure: illegal arguments: $e")
      ValNull
    }
  }

  private def parseDate(d: String): Val = {
    if (isValidDate(d)) {
      Try(ValDate(d)).getOrElse {
        logger.warn(s"Failed to parse date from '$d'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse date from '$d'");
      ValNull
    }
  }

  private def parseTime(t: String): Val = {
    if (isOffsetTime(t)) {
      Try(ValTime(t)).getOrElse {
        logger.warn(s"Failed to parse time from '$t'");
        ValNull
      }
    } else {
      Try(ValLocalTime(t)).getOrElse {
        logger.warn(s"Failed to parse local-time from '$t'");
        ValNull
      }
    }
  }

  private def parseDateTime(dt: String): Val = {
    if (isValidDate(dt)) {
      Try(ValLocalDateTime((dt: Date).atTime(0, 0))).getOrElse {
        logger.warn(s"Failed to parse date(-time) from '$dt'");
        ValNull
      }
    } else if (isOffsetDateTime(dt)) {
      Try(ValDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse date-time from '$dt'");
        ValNull
      }
    } else if (isLocalDateTime(dt)) {
      Try(ValLocalDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse local-date-time from '$dt'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse date-time from '$dt'");
      ValNull
    }
  }

  private def parseDuration(d: String): Val = {
    if (isYearMonthDuration(d)) {
      Try(ValYearMonthDuration((d: YearMonthDuration).normalized)).getOrElse {
        logger.warn(s"Failed to parse year-month-duration from '$d'");
        ValNull
      }
    } else if (isDayTimeDuration(d)) {
      Try(ValDayTimeDuration(d)).getOrElse {
        logger.warn(s"Failed to parse day-time-duration from '$d'");
        ValNull
      }
    } else {
      logger.warn(s"Failed to parse duration from '$d'");
      ValNull
    }
  }

  // conversion functions

  def dateFunction =
    ValFunction(
      List("from"),
      _ match {
        case List(ValString(from))        => parseDate(from)
        case List(ValLocalDateTime(from)) => ValDate(from.toLocalDate())
        case List(ValDateTime(from))      => ValDate(from.toLocalDate())
        case List(ValDate(from))          => ValDate(from)
        case e                            => error(e)
      }
    )

  def dateFunction3 =
    ValFunction(
      List("year", "month", "day"),
      _ match {
        case List(ValNumber(year), ValNumber(month), ValNumber(day)) =>
          Try {
            ValDate(LocalDate.of(year.intValue, month.intValue, day.intValue))
          }.getOrElse {
            logger.warn(
              s"Failed to parse date from: year=$year, month=$month, day=$day");
            ValNull
          }
        case e => error(e)
      }
    )

  def dateTime =
    ValFunction(List("from"), _ match {
      case List(ValString(from)) => parseDateTime(from)
      case e                     => error(e)
    })

  def dateTime2 =
    ValFunction(
      List("date", "time"),
      _ match {
        case List(ValDate(date), ValLocalTime(time)) =>
          ValLocalDateTime(date.atTime(time))
        case List(ValDate(date), ValTime(time)) =>
          ValDateTime(time.withDate(date))
        case List(ValLocalDateTime(dateTime), ValLocalTime(time)) =>
          ValLocalDateTime(dateTime.toLocalDate().atTime(time))
        case List(ValLocalDateTime(dateTime), ValTime(time)) =>
          ValDateTime(time.withDate(dateTime.toLocalDate()))
        case List(ValDateTime(dateTime), ValLocalTime(time)) =>
          ValLocalDateTime(dateTime.toLocalDate().atTime(time))
        case List(ValDateTime(dateTime), ValTime(time)) =>
          ValDateTime(time.withDate(dateTime.toLocalDate()))
        case e => error(e)
      }
    )

  def timeFunction =
    ValFunction(
      List("from"),
      _ match {
        case List(ValString(from))        => parseTime(from)
        case List(ValLocalDateTime(from)) => ValLocalTime(from.toLocalTime())
        case List(ValDateTime(from))      => ValTime(ZonedTime.of(from))
        case List(ValDate(from)) =>
          ValTime(ZonedTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC))
        case e => error(e)
      }
    )

  def timeFunction3 =
    ValFunction(
      List("hour", "minute", "second"),
      _ match {
        case List(ValNumber(hour), ValNumber(minute), ValNumber(second)) =>
          Try {
            val nanos = second.bigDecimal
              .remainder(BigDecimal.ONE)
              .movePointRight(9)
              .intValue
            ValLocalTime(
              LocalTime
                .of(hour.intValue, minute.intValue, second.intValue, nanos))
          }.getOrElse {
            logger.warn(
              s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second");
            ValNull
          }
        case e => error(e)
      }
    )

  def timeFunction4 =
    ValFunction(
      List("hour", "minute", "second", "offset"),
      _ match {
        case List(ValNumber(hour),
        ValNumber(minute),
        ValNumber(second),
        ValDayTimeDuration(offset)) =>
          Try {
            val nanos = second.bigDecimal
              .remainder(BigDecimal.ONE)
              .movePointRight(9)
              .intValue
            val localTime = LocalTime.of(hour.intValue,
              minute.intValue,
              second.intValue,
              nanos)
            val zonedOffset = ZoneOffset.ofTotalSeconds(offset.getSeconds.toInt)

            ValTime(ZonedTime.of(localTime, zonedOffset))
          }.getOrElse {
            logger.warn(
              s"Failed to parse time from: hour=$hour, minute=$minute, second=$second, offset=$offset");
            ValNull
          }
        case List(ValNumber(hour),
        ValNumber(minute),
        ValNumber(second),
        ValNull) =>
          Try {
            ValLocalTime(
              LocalTime.of(hour.intValue, minute.intValue, second.intValue))
          }.getOrElse {
            logger.warn(
              s"Failed to parse local-time from: hour=$hour, minute=$minute, second=$second");
            ValNull
          }
        case e => error(e)
      }
    )

  def numberFunction =
    ValFunction(List("from"), _ match {
      case List(ValString(from)) => ValNumber(from)
      case e                     => error(e)
    })

  def numberFunction2 =
    ValFunction(
      List("from", "grouping separator"),
      _ match {
        case List(ValString(from), ValString(grouping))
          if (isValidGroupingSeparator(grouping)) =>
          ValNumber(from.replace(grouping, ""))
        case List(ValString(from), ValString(grouping)) =>
          ValError(
            s"illegal argument for grouping. Must be one of ' ', ',' or '.'")
        case e => error(e)
      }
    )

  def numberFunction3 =
    ValFunction(
      List("from", "grouping separator", "decimal separator"),
      _ match {
        case List(ValString(from), ValString(grouping), ValString(decimal))
          if (isValidGroupingSeparator(grouping) && isValidDecimalSeparator(
            decimal) && grouping != decimal) =>
          ValNumber(from.replace(grouping, "").replace(decimal, "."))
        case List(ValString(from), ValString(grouping), ValString(decimal)) =>
          ValError(
            s"illegal arguments for grouping or decimal. Must be one of ' ' (grouping only), ',' or '.'")
        case e => error(e)
      }
    )

  private def isValidGroupingSeparator(separator: String) =
    separator == " " || separator == "," || separator == "."

  private def isValidDecimalSeparator(separator: String) =
    separator == "," || separator == "."

  lazy val dateTimeOffsetZoneIdPattern =
    Pattern.compile("(.*)([+-]\\d{2}:\\d{2}|Z)(@.*)")

  def stringFunction =
    ValFunction(
      List("from"),
      _ match {
        case List(ValString(from))  => ValString(from)
        case List(ValBoolean(from)) => ValString(from.toString)
        case List(ValNumber(from))  => ValString(from.toString)
        case List(ValDate(from))    => ValString(from.format(dateFormatter))
        case List(ValLocalTime(from)) =>
          ValString(from.format(localTimeFormatter))
        case List(ValTime(from)) => ValString(from.format)
        case List(ValLocalDateTime(from)) =>
          ValString(from.format(localDateTimeFormatter))
        case List(ValDateTime(from)) => {
          val formattedDateTime = from.format(dateTimeFormatter)
          // remove offset-id if zone-id is present
          val dateTimeWithOffsetOrZoneId = dateTimeOffsetZoneIdPattern
            .matcher(formattedDateTime)
            .replaceAll("$1$3")
          ValString(dateTimeWithOffsetOrZoneId)
        }
        case List(ValYearMonthDuration(from)) => ValString(from.toString)
        case List(ValDayTimeDuration(from))   => ValString(from.toString)
        case e                                => error(e)
      }
    )

  def durationFunction =
    ValFunction(List("from"), _ match {
      case List(ValString(from)) => parseDuration(from)
      case e                     => error(e)
    })

  def durationFunction2 =
    ValFunction(
      List("from", "to"),
      _ match {
        case List(ValDate(from), ValDate(to)) =>
          ValYearMonthDuration(Period.between(from, to).withDays(0).normalized)
        case List(ValLocalDateTime(from), ValLocalDateTime(to)) =>
          ValYearMonthDuration(
            Period
              .between(from.toLocalDate, to.toLocalDate)
              .withDays(0)
              .normalized)
        case List(ValDateTime(from), ValDateTime(to)) =>
          ValYearMonthDuration(
            Period
              .between(from.toLocalDate, to.toLocalDate)
              .withDays(0)
              .normalized)
        case List(ValDateTime(from), ValLocalDateTime(to)) =>
          ValYearMonthDuration(
            Period
              .between(from.toLocalDate, to.toLocalDate)
              .withDays(0)
              .normalized)
        case List(ValLocalDateTime(from), ValDateTime(to)) =>
          ValYearMonthDuration(
            Period
              .between(from.toLocalDate, to.toLocalDate)
              .withDays(0)
              .normalized)
        case List(ValDate(from), ValDateTime(to)) =>
          ValYearMonthDuration(
            Period.between(from, to.toLocalDate()).withDays(0).normalized)
        case List(ValDate(from), ValLocalDateTime(to)) =>
          ValYearMonthDuration(
            Period.between(from, to.toLocalDate()).withDays(0).normalized)
        case List(ValDateTime(from), ValDate(to)) =>
          ValYearMonthDuration(
            Period.between(from.toLocalDate(), to).withDays(0).normalized)
        case List(ValLocalDateTime(from), ValDate(to)) =>
          ValYearMonthDuration(
            Period.between(from.toLocalDate(), to).withDays(0).normalized)
        case e => error(e)
      }
    )

  // boolean functions

  def notFunction =
    ValFunction(List("negand"), _ match {
      case List(ValBoolean(negand)) => ValBoolean(!negand)
      case List(other: Val)         => ValNull
      case other                    => ValNull
    })

  // string functions

  def substringFunction =
    ValFunction(
      List("string", "start position"),
      _ match {
        case List(ValString(string), ValNumber(start)) =>
          ValString(string.substring(stringIndex(string, start.intValue)))
        case e => error(e)
      }
    )

  def substringFunction3 =
    ValFunction(
      List("string", "start position", "length"),
      _ match {
        case List(ValString(string), ValNumber(start), ValNumber(length)) =>
          ValString(
            string.substring(
              stringIndex(string, start.intValue),
              stringIndex(string, start.intValue) + length.intValue))
        case e => error(e)
      }
    )

  private def stringIndex(string: String, index: Int) =
    if (index > 0) {
      index - 1
    } else {
      string.length + index
    }

  def stringLengthFunction =
    ValFunction(List("string"), _ match {
      case List(ValString(string)) => ValNumber(string.length)
      case e                       => error(e)
    })

  def upperCaseFunction =
    ValFunction(List("string"), _ match {
      case List(ValString(string)) => ValString(string.toUpperCase)
      case e                       => error(e)
    })

  def lowerCaseFunction =
    ValFunction(List("string"), _ match {
      case List(ValString(string)) => ValString(string.toLowerCase)
      case e                       => error(e)
    })

  def substringBeforeFunction =
    ValFunction(
      List("string", "match"),
      _ match {
        case List(ValString(string), ValString(m)) => {
          val index = string.indexOf(m)
          if (index > 0) {
            ValString(string.substring(0, index))
          } else {
            ValString("")
          }
        }
        case e => error(e)
      }
    )

  def substringAfterFunction =
    ValFunction(
      List("string", "match"),
      _ match {
        case List(ValString(string), ValString(m)) => {
          val index = string.indexOf(m)
          if (index >= 0) {
            ValString(string.substring(index + m.length))
          } else {
            ValString("")
          }
        }
        case e => error(e)
      }
    )

  def replaceFunction =
    ValFunction(
      List("input", "pattern", "replacement"),
      _ match {
        case List(ValString(input),
        ValString(pattern),
        ValString(replacement)) =>
          ValString(input.replaceAll(pattern, replacement))
        case e => error(e)
      }
    )

  def replaceFunction4 =
    ValFunction(
      List("input", "pattern", "replacement", "flags"),
      _ match {
        case List(ValString(input),
        ValString(pattern),
        ValString(replacement),
        ValString(flags)) => {
          val p = Pattern.compile(pattern, patternFlags(flags))
          val m = p.matcher(input)
          ValString(m.replaceAll(replacement))
        }
        case e => error(e)
      }
    )

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

  def containsFunction =
    ValFunction(List("string", "match"), _ match {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.contains(m))
      case e => error(e)
    })

  def startsWithFunction =
    ValFunction(List("string", "match"), _ match {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.startsWith(m))
      case e => error(e)
    })

  def endsWithFunction =
    ValFunction(List("string", "match"), _ match {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.endsWith(m))
      case e => error(e)
    })

  def matchesFunction =
    ValFunction(
      List("input", "pattern"),
      _ match {
        case List(ValString(input), ValString(pattern)) => {
          val p = Pattern.compile(pattern)
          val m = p.matcher(input)
          ValBoolean(m.find)
        }
        case e => error(e)
      }
    )

  def matchesFunction3 =
    ValFunction(
      List("input", "pattern", "flags"),
      _ match {
        case List(ValString(input), ValString(pattern), ValString(flags)) => {
          val p = Pattern.compile(pattern, patternFlags(flags))
          val m = p.matcher(input)
          ValBoolean(m.find)
        }
        case e => error(e)
      }
    )

  def splitFunction =
    ValFunction(
      List("string", "delimiter"),
      _ match {
        case List(ValString(string), ValString(delimiter)) => {
          val p = Pattern.compile(delimiter)
          val r = p.split(string, -1)
          ValList(r.map(ValString).toList)
        }
        case e => error(e)
      }
    )

  // list functions

  def listContainsFunction =
    ValFunction(List("list", "element"), _ match {
      case List(ValList(list), element) => ValBoolean(list.contains(element))
      case e                            => error(e)
    })

  def countFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => ValNumber(list.size)
      case e                   => error(e)
    })

  def minFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(l @ ValList(list)) =>
          list match {
            case Nil                   => ValNull
            case _ if (l.isComparable) => list.min
            case _                     => logger.warn(s"$l is not comparable"); ValNull
          }
        case e => error(e)
      },
      hasVarArgs = true
    )

  def maxFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(l @ ValList(list)) =>
          list match {
            case Nil                   => ValNull
            case _ if (l.isComparable) => list.max
            case _                     => logger.warn(s"$l is not comparable"); ValNull
          }
        case e => error(e)
      },
      hasVarArgs = true
    )

  def sumFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) if list.isEmpty => ValNull
        case List(ValList(list)) =>
          withListOfNumbers(list, numbers => ValNumber(numbers.sum))
        case e => error(e)
      },
      hasVarArgs = true
    )

  def productFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) if list.isEmpty => ValNull
        case List(ValList(list)) =>
          withListOfNumbers(list, numbers => ValNumber(numbers.product))
        case e => error(e)
      },
      hasVarArgs = true
    )

  def meanFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) =>
          list match {
            case Nil => ValNull
            case l =>
              withListOfNumbers(
                list,
                numbers => ValNumber(numbers.sum / numbers.size))
          }
        case e => error(e)
      },
      hasVarArgs = true
    )

  def medianFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) if list.isEmpty => ValNull
        case List(ValList(list)) =>
          withListOfNumbers(
            list,
            numbers => {
              val sortedList = numbers.sorted

              if (list.size % 2 == 1) {
                ValNumber(sortedList(list.size / 2))
              } else {
                val i = list.size / 2
                val x = sortedList(i - 1)
                val y = sortedList(i)
                ValNumber((x + y) / 2)
              }
            }
          )
        case e => error(e)
      },
      hasVarArgs = true
    )

  def stddevFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) if list.isEmpty => ValNull
        case List(ValList(list)) =>
          withListOfNumbers(
            list,
            numbers => {

              val sum = numbers.sum
              val mean = sum / numbers.size

              val d = ((0: Number) /: numbers) {
                case (dev, n) => dev + (n - mean).pow(2)
              }

              val stddev = Math.sqrt((d / (numbers.size - 1)).toDouble)

              ValNumber(stddev)
            }
          )
        case e => error(e)
      },
      hasVarArgs = true
    )

  def modeFunction =
    ValFunction(
      List("list"),
      _ match {
        case List(ValList(list)) if list.isEmpty => ValList(List.empty)
        case List(ValList(list)) =>
          withListOfNumbers(
            list,
            numbers => {

              val sortedList = numbers
                .groupBy(n => n)
                .map { case (n, ns) => n -> ns.size }
                .toList
                .sortBy { case (n, count) => count }
                .reverse

              val maxCount = sortedList.head._2

              val modeElements = sortedList
                .takeWhile { case (n, count) => count == maxCount }
                .map(_._1)
                .sorted

              ValList(modeElements.map(ValNumber))
            }
          )
        case e => error(e)
      },
      hasVarArgs = true
    )

  def andFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => all(list)
      case e                   => error(e)
    }, hasVarArgs = true)

  private def all(xs: List[Val]): Val = xs match {
    case Nil => ValBoolean(true)
    case x :: xs =>
      x match {
        case ValBoolean(false) => ValBoolean(false)
        case ValBoolean(true)  => all(xs)
        case other =>
          all(xs) match {
            case ValBoolean(false) => ValBoolean(false)
            case _                 => ValNull
          }
      }
  }

  def orFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => atLeastOne(list)
      case e                   => error(e)
    }, hasVarArgs = true)

  private def atLeastOne(xs: List[Val]): Val = xs match {
    case Nil => ValBoolean(false)
    case x :: xs =>
      x match {
        case ValBoolean(true)  => ValBoolean(true)
        case ValBoolean(false) => atLeastOne(xs)
        case other =>
          atLeastOne(xs) match {
            case ValBoolean(true) => ValBoolean(true)
            case _                => ValNull
          }
      }
  }

  def sublistFunction =
    ValFunction(List("list", "start"), _ match {
      case List(ValList(list), ValNumber(start)) =>
        ValList(list.slice(listIndex(list, start.intValue), list.length))
      case e => error(e)
    })

  def sublistFunction3 =
    ValFunction(
      List("list", "start", "length"),
      _ match {
        case List(ValList(list), ValNumber(start), ValNumber(length)) =>
          ValList(
            list.slice(listIndex(list, start.intValue),
              listIndex(list, start.intValue) + length.intValue))
        case e => error(e)
      }
    )

  private def listIndex(list: List[_], index: Int) =
    if (index > 0) {
      index - 1
    } else {
      list.size + index
    }

  def appendFunction =
    ValFunction(List("list", "items"), _ match {
      case List(ValList(list), ValList(items)) => ValList(list ++ items)
      case e                                   => error(e)
    }, hasVarArgs = true)

  def concatenateFunction =
    ValFunction(
      List("lists"),
      _ match {
        case List(ValList(lists)) =>
          ValList(
            lists
              .flatMap(_ match {
                case ValList(list) => list
                case v             => List(v)
              })
              .toList)
        case e => error(e)
      },
      hasVarArgs = true
    )

  def insertBeforeFunction =
    ValFunction(
      List("list", "position", "newItem"),
      _ match {
        case List(ValList(list), ValNumber(position), newItem: Val) =>
          ValList(
            list.take(listIndex(list, position.intValue)) ++ (newItem :: Nil) ++ list
              .drop(listIndex(list, position.intValue)))
        case e => error(e)
      }
    )

  def removeFunction =
    ValFunction(
      List("list", "position"),
      _ match {
        case List(ValList(list), ValNumber(position)) =>
          ValList(
            list.take(listIndex(list, position.intValue)) ++ list.drop(
              listIndex(list, position.intValue + 1)))
        case e => error(e)
      }
    )

  def reverseFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => ValList(list.reverse)
      case e                   => error(e)
    })

  def indexOfFunction =
    ValFunction(List("list", "match"), _ match {
      case List(ValList(list), m: Val) =>
        ValList(indexOfList(list, m) map (ValNumber(_)))
      case e => error(e)
    })

  @tailrec
  private def indexOfList(list: List[Val],
                          item: Val,
                          from: Int = 0,
                          indexList: List[Int] = List()): List[Int] = {
    val index = list.indexOf(item, from)

    if (index >= 0) {
      indexOfList(list, item, index + 1, indexList ++ List(index + 1))
    } else {
      indexList
    }
  }

  def unionFunction =
    ValFunction(
      List("lists"),
      _ match {
        case List(ValList(lists)) =>
          ValList(
            lists
              .flatMap(_ match {
                case ValList(list) => list
                case v             => List(v)
              })
              .toList
              .distinct)
        case e => error(e)
      },
      hasVarArgs = true
    )

  def distinctValuesFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => ValList(list.distinct)
      case e                   => error(e)
    })

  def flattenFunction =
    ValFunction(List("list"), _ match {
      case List(ValList(list)) => ValList(flatten(list))
      case e                   => error(e)
    })

  private def flatten(list: List[Val]): List[Val] = list match {
    case Nil              => Nil
    case ValList(l) :: xs => flatten(l) ++ flatten(xs)
    case x :: xs          => x :: flatten(xs)
  }

  def sortFunction =
    ValFunction(
      List("list", "precedes"),
      _ match {
        case List(ValList(list), ValFunction(params, f, _))
          if (params.size == 2) => {
          try {
            ValList(list.sortWith {
              case (x, y) =>
                f(List(x, y)) match {
                  case ValBoolean(isMet) => isMet
                  case e =>
                    throw new RuntimeException(
                      s"expected boolean but found '$e'")
                }
            })
          } catch {
            case e: Throwable =>
              ValError(
                s"fail to sort list by given precedes function: ${e.getMessage}")
          }
        }
        case List(ValList(list), ValFunction(params, _, _)) =>
          ValError(
            s"expect boolean function with 2 arguments, but found '${params.size}'")
        case e => error(e)
      }
    )

  // number functions

  def decimalFunction =
    ValFunction(List("n", "scale"), _ match {
      case List(ValNumber(n), ValNumber(scale)) =>
        ValNumber(n.setScale(scale.intValue, RoundingMode.HALF_EVEN))
      case e => error(e)
    })

  def decimalFunction3 =
    ValFunction(List("n", "scale", "mode"), _ match {
      case List(ValNumber(n), ValNumber(scale), ValString(mode))
        if(isValidRoundingMode(mode)) =>
        ValNumber(n.setScale(scale.intValue, RoundingMode.withName(mode.toUpperCase)))
      case List(ValNumber(n), ValNumber(scale), ValString(mode)) =>
        ValError(
          s"illegal arguments for rounding mode. " +
            s"Must be one of 'CEILING', 'DOWN', 'FLOOR', 'HALF_DOWN', 'HALF_EVEN', 'HALF_UP', 'UNNECESSARY' or 'UP'.")
      case e => error(e)
    })

  private def isValidRoundingMode(mode: String) =
    mode.equalsIgnoreCase(RoundingMode.CEILING.toString) ||
      mode.equalsIgnoreCase(RoundingMode.DOWN.toString) ||
      mode.equalsIgnoreCase(RoundingMode.FLOOR.toString) ||
      mode.equalsIgnoreCase(RoundingMode.HALF_DOWN.toString) ||
      mode.equalsIgnoreCase(RoundingMode.HALF_EVEN.toString) ||
      mode.equalsIgnoreCase(RoundingMode.HALF_UP.toString) ||
      mode.equalsIgnoreCase(RoundingMode.UNNECESSARY.toString) ||
      mode.equalsIgnoreCase(RoundingMode.UP.toString)

  def floorFunction =
    ValFunction(List("n"), _ match {
      case List(ValNumber(n)) => ValNumber(n.setScale(0, RoundingMode.FLOOR))
      case e                  => error(e)
    })

  def ceilingFunction =
    ValFunction(List("n"), _ match {
      case List(ValNumber(n)) => ValNumber(n.setScale(0, RoundingMode.CEILING))
      case e                  => error(e)
    })

  def absFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) => ValNumber(n.abs)
      case e                  => error(e)
    })

  def moduloFunction =
    ValFunction(List("dividend", "divisor"), _ match {
      case List(ValNumber(dividend), ValNumber(divisor)) =>
        ValNumber(dividend % divisor)
      case e => error(e)
    })

  def sqrtFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) if n < 0 => ValNull
      case List(ValNumber(n))          => ValNumber(Math.sqrt(n.toDouble))
      case e                           => error(e)
    })

  def logFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) => ValNumber(Math.log(n.toDouble))
      case e                  => error(e)
    })

  def expFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) => ValNumber(Math.exp(n.toDouble))
      case e                  => error(e)
    })

  def oddFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) => ValBoolean(n % 2 == 1)
      case e                  => error(e)
    })

  def evenFunction =
    ValFunction(List("number"), _ match {
      case List(ValNumber(n)) => ValBoolean(n % 2 == 0)
      case e                  => error(e)
    })

  // context functions

  def getEntriesFunction =
    ValFunction(
      List("context"),
      _ match {
        case List(ValContext(c: Context)) =>
          c.variableProvider.getVariables.map {
            case (key, value) =>
              Map("key" -> ValString(key), "value" -> value)
          }.toList
        case e => error(e)
      }
    )

  def getValueFunction =
    ValFunction(
      List("context", "key"),
      _ match {
        case List(ValContext(c), ValString(key)) =>
          c.variableProvider
            .getVariable(key)
            .getOrElse(ValNull)
        case e => error(e)
      }
    )

  private def withListOfNumbers(list: List[Val],
                                f: List[Number] => Val): Val = {
    list
      .map(_ match {
        case n: ValNumber => n
        case x            => ValError(s"expected number but found '$x'")
      })
      .find(_.isInstanceOf[ValError]) match {
      case Some(e) => e
      case None    => f(list.asInstanceOf[List[ValNumber]].map(_.value))
    }
  }

}
