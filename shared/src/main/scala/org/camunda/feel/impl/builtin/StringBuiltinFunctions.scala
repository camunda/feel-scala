package org.camunda.feel.impl.builtin

import java.util.regex.Pattern

import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree._

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
            stringIndex(string, start.intValue) + length.intValue)
        )
    }
  )

  private def stringIndex(string: String, index: Int) =
    if (index > 0) {
      index - 1
    } else {
      string.length + index
    }

  private def stringLengthFunction =
    builtinFunction(
      params = List("string"),
      invoke = {
        case List(ValString(string)) =>
          ValNumber(string.length)
      }
    )

  private def upperCaseFunction =
    builtinFunction(
      params = List("string"),
      invoke = {
        case List(ValString(string)) =>
          ValString(string.toUpperCase)
      }
    )

  private def lowerCaseFunction =
    builtinFunction(
      params = List("string"),
      invoke = {
        case List(ValString(string)) =>
          ValString(string.toLowerCase)
      }
    )

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

  val replacementRegex = "\\$(\\d+)".r
  private def replaceFunction = builtinFunction(
    params = List("input", "pattern", "replacement"),
    invoke = {
      case List(ValString(input), ValString(pattern), ValString(replacement)) =>
        // TODO Remove this workaround as soon as https://github.com/scala-js/scala-js/pull/4356/ is merged and released
        //   Probably Scala-JS >= 1.3.2
        val matches = pattern.r.unanchored
          .unapplySeq(input)
          .toSeq
          .flatten
          .filterNot(_ == null)
        val replaced =
          replacementRegex.replaceAllIn(
            replacement,
            m => matches.applyOrElse(m.group(1).toInt - 1, (_: Int) => ""))
        val result = input.replaceAll(pattern, replaced)
        ValString(result)
    }
  )

  private def replaceFunction4 = builtinFunction(
    params = List("input", "pattern", "replacement", "flags"),
    invoke = {
      case List(ValString(input),
                ValString(pattern),
                ValString(replacement),
                ValString(flags)) => {
        val p = Pattern.compile(pattern, patternFlags(flags))
        val m = p.matcher(input)
        ValString(m.replaceAll(replacement))
      }
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
    builtinFunction(
      params = List("string", "match"),
      invoke = {
        case List(ValString(string), ValString(m)) =>
          ValBoolean(string.contains(m))
      }
    )

  private def startsWithFunction =
    builtinFunction(
      params = List("string", "match"),
      invoke = {
        case List(ValString(string), ValString(m)) =>
          ValBoolean(string.startsWith(m))
      }
    )

  private def endsWithFunction =
    builtinFunction(
      params = List("string", "match"),
      invoke = {
        case List(ValString(string), ValString(m)) =>
          ValBoolean(string.endsWith(m))
      }
    )

  private def matchesFunction = builtinFunction(
    params = List("input", "pattern"),
    invoke = {
      case List(ValString(input), ValString(pattern)) => {
        val p = Pattern.compile(pattern)
        val m = p.matcher(input)
        ValBoolean(m.find)
      }
    }
  )

  private def matchesFunction3 = builtinFunction(
    params = List("input", "pattern", "flags"),
    invoke = {
      case List(ValString(input), ValString(pattern), ValString(flags)) => {
        val p = Pattern.compile(pattern, patternFlags(flags))
        val m = p.matcher(input)
        ValBoolean(m.find)
      }
    }
  )

  private def splitFunction = builtinFunction(
    params = List("string", "delimiter"),
    invoke = {
      case List(ValString(string), ValString(delimiter)) => {
        val p = Pattern.compile(delimiter)
        val r = p.split(string, -1)
        ValList(r.map(ValString).toList)
      }
    }
  )

}
