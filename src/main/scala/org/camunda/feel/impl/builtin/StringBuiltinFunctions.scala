package org.camunda.feel.impl.builtin

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._

import java.util.regex.Pattern
import scala.util.Try

object StringBuiltinFunctions {

  def functions = Map(
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

  private def substringFunction = builtinFunction(
    params = List("string", "start position"),
    invoke = {
      case List(ValString(string), ValNumber(start)) =>
        ValString(string.substring(stringIndex(string, start.intValue)))
    }
  )

  private def substringFunction3 = builtinFunction(
    params = List("string", "start position", "length"),
    invoke = {
      case List(ValString(string), ValNumber(start), ValNumber(length)) =>
        ValString(
          string.substring(
            stringIndex(string, start.intValue),
            stringIndex(string, start.intValue) + length.intValue))
    }
  )

  private def stringIndex(string: String, index: Int) =
    if (index > 0) {
      index - 1
    } else {
      string.length + index
    }

  private def stringLengthFunction =
    builtinFunction(params = List("string"), invoke = {
      case List(ValString(string)) => ValNumber(string.length)
    })

  private def upperCaseFunction =
    builtinFunction(params = List("string"), invoke = {
      case List(ValString(string)) => ValString(string.toUpperCase)
    })

  private def lowerCaseFunction =
    builtinFunction(params = List("string"), invoke = {
      case List(ValString(string)) => ValString(string.toLowerCase)
    })

  private def substringBeforeFunction = builtinFunction(
    params = List("string", "match"),
    invoke = {
      case List(ValString(string), ValString(m)) => {
        val index = string.indexOf(m)
        if (index > 0) {
          ValString(string.substring(0, index))
        } else {
          ValString("")
        }
      }
    }
  )

  private def substringAfterFunction = builtinFunction(
    params = List("string", "match"),
    invoke = {
      case List(ValString(string), ValString(m)) => {
        val index = string.indexOf(m)
        if (index >= 0) {
          ValString(string.substring(index + m.length))
        } else {
          ValString("")
        }
      }
    }
  )

  private def replaceFunction = builtinFunction(
    params = List("input", "pattern", "replacement"),
    invoke = {
      case List(ValString(input), ValString(pattern), ValString(replacement)) =>
        Try(Pattern.compile(pattern))
          .map { pattern =>
            val m = pattern.matcher(input)
            ValString(m.replaceAll(replacement))
          }
          .recover { _ =>
            ValError(s"Invalid pattern '$pattern'")
          }
          .get
    }
  )

  private def replaceFunction4 = builtinFunction(
    params = List("input", "pattern", "replacement", "flags"),
    invoke = {
      case List(ValString(input),
                ValString(pattern),
                ValString(replacement),
                ValString(flags)) =>
        Try(Pattern.compile(pattern, patternFlags(flags)))
          .map { pattern =>
            val m = pattern.matcher(input)
            ValString(m.replaceAll(replacement))
          }
          .recover { _ =>
            ValError(s"Invalid pattern '$pattern'")
          }
          .get
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

  private def containsFunction =
    builtinFunction(params = List("string", "match"), invoke = {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.contains(m))
    })

  private def startsWithFunction =
    builtinFunction(params = List("string", "match"), invoke = {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.startsWith(m))
    })

  private def endsWithFunction =
    builtinFunction(params = List("string", "match"), invoke = {
      case List(ValString(string), ValString(m)) =>
        ValBoolean(string.endsWith(m))
    })

  private def matchesFunction = builtinFunction(
    params = List("input", "pattern"),
    invoke = {
      case List(ValString(input), ValString(pattern)) => {
        Try(Pattern.compile(pattern))
          .map { pattern =>
            val m = pattern.matcher(input)
            ValBoolean(m.find)
          }
          .recover { _ =>
            ValError(s"Invalid pattern '$pattern'")
          }
          .get
      }
    }
  )

  private def matchesFunction3 = builtinFunction(
    params = List("input", "pattern", "flags"),
    invoke = {
      case List(ValString(input), ValString(pattern), ValString(flags)) =>
        Try(Pattern.compile(pattern, patternFlags(flags)))
          .map { pattern =>
            val m = pattern.matcher(input)
            ValBoolean(m.find)
          }
          .recover { _ =>
            ValError(s"Invalid pattern '$pattern'")
          }
          .get
    }
  )

  private def splitFunction = builtinFunction(
    params = List("string", "delimiter"),
    invoke = {
      case List(ValString(string), ValString(delimiter)) =>
        Try(Pattern.compile(delimiter))
          .map { pattern =>
            val r = pattern.split(string, -1)
            ValList(r.map(ValString).toList)
          }
          .recover { _ =>
            ValError(s"Invalid pattern for delimiter '$delimiter'")
          }
          .get
    }
  )

}
