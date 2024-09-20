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
package org.camunda.feel.impl.builtin

import com.fasterxml.uuid.Generators
import org.camunda.feel.impl.builtin.BuiltinFunction.builtinFunction
import org.camunda.feel.syntaxtree.{ValBoolean, ValError, ValList, ValNumber, ValString}

import java.util.Base64
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import scala.util.Try

object StringBuiltinFunctions {

  private lazy val generator = Generators.timeBasedEpochRandomGenerator()

  def functions = Map(
    "substring"        -> List(substringFunction, substringFunction3),
    "string length"    -> List(stringLengthFunction),
    "upper case"       -> List(upperCaseFunction),
    "lower case"       -> List(lowerCaseFunction),
    "substring before" -> List(substringBeforeFunction),
    "substring after"  -> List(substringAfterFunction),
    "replace"          -> List(replaceFunction, replaceFunction4),
    "contains"         -> List(containsFunction),
    "starts with"      -> List(startsWithFunction),
    "ends with"        -> List(endsWithFunction),
    "matches"          -> List(matchesFunction, matchesFunction3),
    "split"            -> List(splitFunction),
    "extract"          -> List(extractFunction),
    "trim"             -> List(trimFunction),
    "uuid"             -> List(uuidFunction),
    "to base64"        -> List(toBase64Function),
    "is blank"         -> List(isBlankFunction)
  )

  private def substringFunction = builtinFunction(
    params = List("string", "start position"),
    invoke = { case List(ValString(string), ValNumber(start)) =>
      ValString(string.substring(stringIndex(string, start.intValue)))
    }
  )

  private def substringFunction3 = builtinFunction(
    params = List("string", "start position", "length"),
    invoke = { case List(ValString(string), ValNumber(start), ValNumber(length)) =>
      val startIndex = stringIndex(string, start.intValue)
      val endIndex   = Math.min(startIndex + length.intValue, string.length)

      ValString(
        string.substring(startIndex, endIndex)
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
      invoke = { case List(ValString(string)) =>
        ValNumber(string.length)
      }
    )

  private def upperCaseFunction =
    builtinFunction(
      params = List("string"),
      invoke = { case List(ValString(string)) =>
        ValString(string.toUpperCase)
      }
    )

  private def lowerCaseFunction =
    builtinFunction(
      params = List("string"),
      invoke = { case List(ValString(string)) =>
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

  private def replaceFunction = builtinFunction(
    params = List("input", "pattern", "replacement"),
    invoke = { case List(ValString(input), ValString(pattern), ValString(replacement)) =>
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
      case List(ValString(input), ValString(pattern), ValString(replacement), ValString(flags)) =>
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
    builtinFunction(
      params = List("string", "match"),
      invoke = { case List(ValString(string), ValString(m)) =>
        ValBoolean(string.contains(m))
      }
    )

  private def startsWithFunction =
    builtinFunction(
      params = List("string", "match"),
      invoke = { case List(ValString(string), ValString(m)) =>
        ValBoolean(string.startsWith(m))
      }
    )

  private def endsWithFunction =
    builtinFunction(
      params = List("string", "match"),
      invoke = { case List(ValString(string), ValString(m)) =>
        ValBoolean(string.endsWith(m))
      }
    )

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
    invoke = { case List(ValString(input), ValString(pattern), ValString(flags)) =>
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
    invoke = { case List(ValString(string), ValString(delimiter)) =>
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

  private def extractFunction = builtinFunction(
    params = List("input", "pattern"),
    invoke = { case List(ValString(input), ValString(pattern)) =>
      Try(pattern.r)
        .map { regex =>
          val matches = regex.findAllIn(input).map(ValString)
          ValList(matches.toList)
        }
        .recover { _ =>
          ValError(s"Invalid pattern '$pattern'")
        }
        .get
    }
  )

  private def trimFunction =
    builtinFunction(
      params = List("string"),
      invoke = { case List(ValString(string)) =>
        ValString(string.trim)
      }
    )

  private def uuidFunction =
    builtinFunction(
      params = List(),
      invoke = { case List() =>
        ValString(generator.generate.toString)
      }
    )

  private def toBase64Function =
    builtinFunction(
      params = List("value"),
      invoke = { case List(ValString(value)) =>
        val bytes = value.getBytes(StandardCharsets.UTF_8)
        ValString(Base64.getEncoder.encodeToString(bytes))
      }
    )

  private def isBlankFunction = builtinFunction(
    params = List("string"),
    invoke = {case List(ValString(string)) =>
      ValBoolean(string.isBlank)
    }
  )

}
