package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._
import org.camunda.feel.{Number, logger}

import scala.annotation.tailrec

object ListBuiltinFunctions {

  def functions = Map(
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
    "sort" -> List(sortFunction),
    "join" -> List(joinFunction,
                   joinWithDelimiterFunction,
                   joinWithDelimiterAndPrefixAndSuffixFunction)
  )

  private def listContainsFunction =
    builtinFunction(params = List("list", "element"), invoke = {
      case List(ValList(list), element) => ValBoolean(list.contains(element))
    })

  private def countFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => ValNumber(list.size)
    })

  private def minFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(l @ ValList(list)) =>
        list match {
          case Nil                   => ValNull
          case _ if (l.isComparable) => list.min
          case _                     => logger.warn(s"$l is not comparable"); ValNull
        }
    },
    hasVarArgs = true
  )

  private def maxFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(l @ ValList(list)) =>
        list match {
          case Nil                   => ValNull
          case _ if (l.isComparable) => list.max
          case _                     => logger.warn(s"$l is not comparable"); ValNull
        }
    },
    hasVarArgs = true
  )

  private def sumFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(ValList(list)) if list.isEmpty => ValNull
      case List(ValList(list)) =>
        withListOfNumbers(list, numbers => ValNumber(numbers.sum))
    },
    hasVarArgs = true
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

  private def productFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(ValList(list)) if list.isEmpty => ValNull
      case List(ValList(list)) =>
        withListOfNumbers(list, numbers => ValNumber(numbers.product))
    },
    hasVarArgs = true
  )

  private def meanFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(ValList(list)) =>
        list match {
          case Nil => ValNull
          case l =>
            withListOfNumbers(list,
                              numbers => ValNumber(numbers.sum / numbers.size))
        }
    },
    hasVarArgs = true
  )

  private def medianFunction = builtinFunction(
    params = List("list"),
    invoke = {
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
    },
    hasVarArgs = true
  )

  private def stddevFunction = builtinFunction(
    params = List("list"),
    invoke = {
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
    },
    hasVarArgs = true
  )

  private def modeFunction = builtinFunction(
    params = List("list"),
    invoke = {
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
    },
    hasVarArgs = true
  )

  private def andFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => all(list)
    }, hasVarArgs = true)

  private def all(items: List[Val]): Val = {
    items.foldLeft[Val](ValBoolean(true)) {
      case (ValBoolean(false), _)               => ValBoolean(false)
      case (ValBoolean(true), item: ValBoolean) => item
      case (ValNull, ValBoolean(false))         => ValBoolean(false)
      case (_, _)                               => ValNull
    }
  }

  private def orFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => atLeastOne(list)
    }, hasVarArgs = true)

  private def atLeastOne(items: List[Val]): Val = {
    items.foldLeft[Val](ValBoolean(false)) {
      case (ValBoolean(true), _)                 => ValBoolean(true)
      case (ValBoolean(false), item: ValBoolean) => item
      case (ValNull, ValBoolean(true))           => ValBoolean(true)
      case (_, _)                                => ValNull
    }
  }

  private def sublistFunction =
    builtinFunction(params = List("list", "start"), invoke = {
      case List(ValList(list), ValNumber(start)) =>
        ValList(list.slice(listIndex(list, start.intValue), list.length))
    })

  private def sublistFunction3 = builtinFunction(
    params = List("list", "start", "length"),
    invoke = {
      case List(ValList(list), ValNumber(start), ValNumber(length)) =>
        ValList(
          list.slice(listIndex(list, start.intValue),
                     listIndex(list, start.intValue) + length.intValue))
    }
  )

  private def listIndex(list: List[_], index: Int) =
    if (index > 0) {
      index - 1
    } else {
      list.size + index
    }

  private def appendFunction =
    builtinFunction(params = List("list", "items"), invoke = {
      case List(ValList(list), ValList(items)) => ValList(list ++ items)
    }, hasVarArgs = true)

  private def concatenateFunction = builtinFunction(
    params = List("lists"),
    invoke = {
      case List(ValList(lists)) =>
        ValList(
          lists
            .flatMap(_ match {
              case ValList(list) => list
              case v             => List(v)
            })
            .toList)
    },
    hasVarArgs = true
  )

  private def insertBeforeFunction = builtinFunction(
    params = List("list", "position", "newItem"),
    invoke = {
      case List(ValList(list), ValNumber(position), newItem: Val) =>
        ValList(list
          .take(listIndex(list, position.intValue)) ++ (newItem :: Nil) ++ list
          .drop(listIndex(list, position.intValue)))
    }
  )

  private def removeFunction = builtinFunction(
    params = List("list", "position"),
    invoke = {
      case List(ValList(list), ValNumber(position)) =>
        ValList(
          list.take(listIndex(list, position.intValue)) ++ list.drop(
            listIndex(list, position.intValue + 1)))
    }
  )

  private def reverseFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => ValList(list.reverse)
    })

  private def indexOfFunction =
    builtinFunction(params = List("list", "match"), invoke = {
      case List(ValList(list), m: Val) =>
        ValList(indexOfList(list, m) map (ValNumber(_)))
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

  private def unionFunction = builtinFunction(
    params = List("lists"),
    invoke = {
      case List(ValList(lists)) =>
        ValList(
          lists
            .flatMap(_ match {
              case ValList(list) => list
              case v             => List(v)
            })
            .toList
            .distinct)
    },
    hasVarArgs = true
  )

  private def distinctValuesFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => ValList(list.distinct)
    })

  private def flattenFunction =
    builtinFunction(params = List("list"), invoke = {
      case List(ValList(list)) => ValList(flatten(list))
    })

  private def flatten(list: List[Val]): List[Val] = {
    list.flatten {
      case ValList(items) => flatten(items)
      case item           => List(item)
    }
  }

  private def sortFunction = builtinFunction(
    params = List("list", "precedes"),
    invoke = {
      case List(ValList(list), ValFunction(params, f, _))
          if (params.size == 2) => {
        try {
          ValList(list.sortWith {
            case (x, y) =>
              f(List(x, y)) match {
                case ValBoolean(isMet) => isMet
                case e =>
                  throw new RuntimeException(s"expected boolean but found '$e'")
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
    }
  )

  private def joinFunction = builtinFunction(
    params = List("list"),
    invoke = {
      case List(ValList(list)) =>
        withListOfStrings(list, strings => ValString(strings.mkString))
    }
  )

  private def joinWithDelimiterFunction = builtinFunction(
    params = List("list", "delimiter"),
    invoke = {
      case List(ValList(list), ValString(delimiter)) =>
        withListOfStrings(list,
                          strings => ValString(strings.mkString(delimiter)))

      case List(ValList(list), ValNull) =>
        withListOfStrings(list, strings => ValString(strings.mkString))
    }
  )

  private def joinWithDelimiterAndPrefixAndSuffixFunction = builtinFunction(
    params = List("list", "delimiter", "prefix", "suffix"),
    invoke = {
      case List(ValList(list),
                ValString(delimiter),
                ValString(prefix),
                ValString(suffix)) =>
        withListOfStrings(
          list,
          strings =>
            ValString(
              strings.mkString(start = prefix, sep = delimiter, end = suffix)))

      case List(ValList(list), ValNull, ValString(prefix), ValString(suffix)) =>
        withListOfStrings(
          list,
          strings =>
            ValString(strings.mkString(start = prefix, sep = "", end = suffix)))
    }
  )

  private def withListOfStrings(list: List[Val],
                                f: List[String] => Val): Val = {
    val strings = list
      .map(elem =>
        elem match {
          case n: ValString => n
          case ValNull      => ValString("")
          case x            => ValError(s"expected string but found '$x'")
      })

    strings.find(_.isInstanceOf[ValError]) match {
      case Some(e) => e
      case None    => f(strings.asInstanceOf[List[ValString]].map(_.value))
    }
  }
}
