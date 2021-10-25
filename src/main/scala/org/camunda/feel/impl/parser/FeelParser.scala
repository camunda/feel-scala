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
package org.camunda.feel.impl.parser

import fastparse.JavaWhitespace.whitespace
import fastparse.{
  AnyChar,
  ByNameOps,
  ByNameOpsStr,
  CharIn,
  CharPred,
  CharsWhile,
  CharsWhileIn,
  EagerOpsStr,
  End,
  LiteralStr,
  P,
  Parsed,
  Start,
  StringIn,
  parse
}
import org.camunda.feel.syntaxtree.{
  Addition,
  ArithmeticNegation,
  AtLeastOne,
  ClosedIntervalBoundary,
  Conjunction,
  ConstBool,
  ConstContext,
  ConstDate,
  ConstDateTime,
  ConstDayTimeDuration,
  ConstInputValue,
  ConstList,
  ConstLocalDateTime,
  ConstLocalTime,
  ConstNull,
  ConstNumber,
  ConstString,
  ConstTime,
  ConstYearMonthDuration,
  Disjunction,
  Division,
  Equal,
  EveryItem,
  Exp,
  Exponentiation,
  Filter,
  For,
  FunctionDefinition,
  FunctionInvocation,
  FunctionParameters,
  GreaterOrEqual,
  GreaterThan,
  If,
  In,
  InputEqualTo,
  InputGreaterOrEqual,
  InputGreaterThan,
  InputLessOrEqual,
  InputLessThan,
  InstanceOf,
  Interval,
  IntervalBoundary,
  JavaFunctionInvocation,
  LessOrEqual,
  LessThan,
  Multiplication,
  NamedFunctionParameters,
  Not,
  OpenIntervalBoundary,
  PathExpression,
  PositionalFunctionParameters,
  QualifiedFunctionInvocation,
  Range,
  Ref,
  SomeItem,
  Subtraction,
  UnaryTestExpression
}
import org.camunda.feel.{
  Date,
  isOffsetDateTime,
  isOffsetTime,
  isValidDate,
  isYearMonthDuration,
  stringToDate,
  stringToDateTime,
  stringToDayTimeDuration,
  stringToLocalDateTime,
  stringToLocalTime,
  stringToTime,
  stringToYearMonthDuration
}

import scala.util.Try

/**
  * The parser is written following the FEEL grammar definition in the DMN specification.
  *
  * In order to understand how the parser works, it is recommended to read the documentation first:
  * [[https://www.lihaoyi.com/fastparse]]. Additional resources:
  * [[https://www.lihaoyi.com/post/EasyParsingwithParserCombinators.html]],
  * [[https://www.lihaoyi.com/post/BuildyourownProgrammingLanguagewithScala.html]]
  */
object FeelParser {

  def parseExpression(expression: String): Parsed[Exp] =
    parse(expression, fullExpression(_))

  def parseUnaryTests(expression: String): Parsed[Exp] =
    parse(expression, fullUnaryExpression(_))

  // --------------- entry parsers ---------------

  private def fullExpression[_: P]: P[Exp] = P(Start ~ expression ~ End)

  private def fullUnaryExpression[_: P]: P[Exp] = P(Start ~ unaryTests ~ End)

  // --------------- common parsers ---------------

  // language key words that can't be used as variable names
  private def reservedWord[_: P]: P[String] =
    P(
      StringIn(
        "null",
        "true",
        "false",
        "function",
        "in"
      )
    ).!

  // an identifier which is not a reserved word. but, it can contain a reserved word.
  private def identifier[_: P]: P[String] =
    P(
      reservedWord.? ~~ javaLikeIdentifier
    ).!

  private def javaLikeIdentifier[_: P]: P[String] =
    P(
      CharPred(Character.isJavaIdentifierStart) ~~ CharsWhile(
        Character.isJavaIdentifierPart,
        0)
    ).!

  // an identifier wrapped in backticks. it can contain any char (e.g. `a b`, `a+b`).
  private def escapedIdentifier[_: P]: P[String] =
    P(
      "`" ~~ (!"`" ~~ AnyChar.!).repX(1) ~~ "`"
    ).map(_.mkString)

  // use only if the identifier is followed by a predefined character (e.g. `(` or `:`)
  private def identifierWithWhitespaces[_: P]: P[String] =
    P(
      identifier ~~ (" " ~~ identifier).repX(1)
    ).!

  private def name[_: P]: P[String] = P(
    identifier | escapedIdentifier
  )

  private def qualifiedName[_: P]: P[List[String]] =
    P(
      name.rep(1, sep = ".")
    ).map(_.toList)

  // a string wrapped in double quotes. it can contain an escape sequences (e.g. \', \", \\, \n, \r, \t, \u269D, \U101EF).
  private def stringWithQuotes[_: P]: P[String] = P(
    "\"" ~~ (("\\" | !"\"") ~~ AnyChar).repX.! ~~ "\""
  )

  // --------------- utility parsers ---------------

  // shortcut function to define an optional parser
  private def optional[_: P](optionalParser: Exp => P[Exp]): Exp => P[Exp] = {
    base =>
      optionalParser(base).?.map(
        _.fold(base)(result => result)
      )
  }

  // --------------- expressions ---------------

  // use different levels to define the precedence of the operators  (i.e. how they can be chained)
  private def expression[_: P]: P[Exp] = expLvl1

  private def expLvl1[_: P]: P[Exp] = ifOp | forOp | quantifiedOp | disjunction

  private def expLvl2[_: P]: P[Exp] = conjunction

  private def expLvl3[_: P]: P[Exp] =
    expLvl4.flatMap(optional(comparison)) | simplePositiveUnaryTest

  private def expLvl4[_: P]: P[Exp] = mathOperator

  private def expLvl5[_: P]: P[Exp] = value

  // --------------- mathematical/arithmetic operators ---------------

  // use different levels to define the precedence of the operators
  private def mathOperator[_: P]: P[Exp] = mathOpLvl1

  private def mathOpLvl1[_: P]: P[Exp] = addSub

  private def mathOpLvl2[_: P]: P[Exp] = mulDiv

  private def mathOpLvl3[_: P]: P[Exp] = exponent

  private def mathOpLvl4[_: P]: P[Exp] = mathNegation

  // --------------- expression parsers ---------------

  private def ifOp[_: P]: P[Exp] =
    P(
      "if" ~ expression ~ "then" ~ expression ~ "else" ~ expression
    ).map {
      case (condition, thenExp, elseExp) => If(condition, thenExp, elseExp)
    }

  private def forOp[_: P]: P[Exp] =
    P(
      "for" ~ listIterator.rep(1, sep = ",") ~ "return" ~ expression
    ).map {
      case (iterators, exp) => For(iterators.toList, exp)
    }

  private def listIterator[_: P]: P[(String, Exp)] = P(
    name ~ "in" ~ (range | value)
  )

  private def range[_: P]: P[Exp] =
    P(
      expLvl4 ~ ".." ~ expLvl4
    ).map {
      case (start, end) => Range(start, end)
    }

  private def quantifiedOp[_: P]: P[Exp] =
    P(
      ("some" | "every").! ~ listIterator
        .rep(1, sep = ",") ~ "satisfies" ~ expression
    ).map {
      case ("some", iterators, condition) =>
        SomeItem(iterators.toList, condition)
      case ("every", iterators, condition) =>
        EveryItem(iterators.toList, condition)
    }

  private def disjunction[_: P]: P[Exp] =
    P(
      expLvl2 ~ ("or" ~ expLvl2).rep
    ).map {
      case (base, ops) => ops.foldLeft(base)(Disjunction)
    }

  private def conjunction[_: P]: P[Exp] =
    P(
      expLvl3 ~ ("and" ~ expLvl3).rep
    ).map {
      case (base, ops) => ops.foldLeft(base)(Conjunction)
    }

  private def comparison[_: P](value: Exp): P[Exp] =
    binaryComparison(value) | between(value) | instanceOf(value) | in(value)

  private def binaryComparison[_: P](x: Exp): P[Exp] =
    P(
      StringIn("<=", ">=", "<", ">", "!=", "=").! ~ expLvl4
    ).map {
      case ("=", y)  => Equal(x, y)
      case ("!=", y) => Not(Equal(x, y))
      case ("<", y)  => LessThan(x, y)
      case ("<=", y) => LessOrEqual(x, y)
      case (">", y)  => GreaterThan(x, y)
      case (">=", y) => GreaterOrEqual(x, y)
    }

  private def between[_: P](x: Exp): P[Exp] =
    P(
      "between" ~ expLvl4 ~ "and" ~ expLvl4
    ).map {
      case (a, b) => Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b))
    }

  private def instanceOf[_: P](value: Exp): P[Exp] =
    P(
      "instance" ~ "of" ~ typeName
    ).map(InstanceOf(value, _))

  private def typeName[_: P]: P[String] =
    P(
      qualifiedName
    ).map(_.mkString("."))

  private def in[_: P](value: Exp): P[Exp] =
    P(
      "in" ~ (("(" ~ positiveUnaryTests ~ ")") | positiveUnaryTest)
    ).map(In(value, _))

  // --------------- mathematical parsers ---------------

  private def addSub[_: P]: P[Exp] =
    P(
      mathOpLvl2 ~ (CharIn("+\\-").! ~ mathOpLvl2).rep
    ).map {
      case (value, ops) =>
        ops.foldLeft(value) {
          case (x, ("+", y)) => Addition(x, y)
          case (x, ("-", y)) => Subtraction(x, y)
        }
    }

  private def mulDiv[_: P]: P[Exp] =
    P(
      mathOpLvl3 ~ (CharIn("*/").! ~ mathOpLvl3).rep
    ).map {
      case (value, ops) =>
        ops.foldLeft(value) {
          case (x, ("*", y)) => Multiplication(x, y)
          case (x, ("/", y)) => Division(x, y)
        }
    }

  private def exponent[_: P]: P[Exp] =
    P(
      mathOpLvl4 ~ ("**" ~ mathOpLvl4).rep
    ).map {
      case (value, ops) => ops.foldLeft(value)(Exponentiation)
    }

  private def mathNegation[_: P]: P[Exp] =
    P(
      "-".!.? ~ expLvl5
    ).map {
      case (Some("-"), value) => ArithmeticNegation(value)
      case (None, value)      => value
    }

  // --------------- value/terminal parsers ---------------

  private def value[_: P]: P[Exp] =
    terminalValue.flatMap(optional(chainedValueOp))

  private def terminalValue[_: P]: P[Exp] =
    temporal | functionInvocation | variableRef | literal | inputValue | functionDefinition | "(" ~ expression ~ ")"

  private def literal[_: P]: P[Exp] =
    nullLiteral | boolean | string | number | temporal | list | context | rangeBoundary

  private def nullLiteral[_: P]: P[Exp] =
    P(
      "null"
    ).map(_ => ConstNull)

  private def boolean[_: P]: P[Exp] =
    P(
      "true" | "false"
    ).!.map {
      case "true"  => ConstBool(true)
      case "false" => ConstBool(false)
    }

  private def string[_: P]: P[Exp] =
    P(
      stringWithQuotes
    ).map(ConstString)

  private def number[_: P]: P[Exp] =
    P(
      "-".? ~~ ((integral ~~ fractional.?) | fractional)
    ).!.map(number => ConstNumber(BigDecimal(number)))

  private def integral[_: P]: P[String] =
    P(
      CharIn("0-9") ~~ digits.?
    ).!

  private def fractional[_: P]: P[String] =
    P(
      "." ~~ digits
    ).!

  private def digits[_: P]: P[String] =
    P(
      CharsWhileIn("0-9")
    ).!

  private def temporal[_: P]: P[Exp] =
    P(
      ("duration" | "date and time" | "date" | "time").! ~ "(" ~ stringWithQuotes ~ ")"
    ).map {
      case ("duration", value)      => parseDuration(value)
      case ("date and time", value) => parseDateTime(value)
      case ("date", value)          => parseDate(value)
      case ("time", value)          => parseTime(value)
    }

  private def list[_: P]: P[Exp] =
    P(
      "[" ~ expression.rep(0, sep = ",") ~ "]"
    ).map(items => ConstList(items.toList))

  private def context[_: P]: P[Exp] =
    P(
      "{" ~ contextEntry.rep(0, sep = ",") ~ "}"
    ).map(entries => ConstContext(entries.toList))

  private def contextEntry[_: P]: P[(String, Exp)] = P(
    (name | stringWithQuotes) ~ ":" ~ expression
  )

  private def variableRef[_: P]: P[Exp] =
    P(
      qualifiedName
    ).map(Ref(_))

  private def inputValue[_: P]: P[Exp] =
    P(
      "?"
    ).map(_ => ConstInputValue)

  private def functionDefinition[_: P]: P[Exp] =
    P(
      "function" ~ "(" ~ parameter
        .rep(0, sep = ",") ~ ")" ~ (externalFunction | expression)
    ).map {
      case (parameters, body) => FunctionDefinition(parameters.toList, body)
    }

  private def parameter[_: P]: P[String] = parameterName

  // parameter names from built-in functions can have whitespaces. the name is limited by `,` or `:`.
  private def parameterName[_: P]: P[String] = identifierWithWhitespaces | name

  private def externalFunction[_: P]: P[Exp] = P(
    "external" ~ externalJavaFunction
  )

  private def externalJavaFunction[_: P]: P[Exp] =
    P(
      "{" ~
        "java" ~ ":" ~ "{" ~
        "class" ~ ":" ~ stringWithQuotes ~ "," ~
        "method signature" ~ ":" ~ javaMethodSignature ~
        "}" ~ "}"
    ).map {
      case (className, (methodName, parameters)) =>
        JavaFunctionInvocation(className, methodName, parameters.toList)
    }

  private def javaMethodSignature[_: P]: P[(String, Seq[String])] = P(
    "\"" ~ name ~ "(" ~ javaMethodParameter_.rep(0, sep = ",") ~ ")" ~ "\""
  )

  private def javaMethodParameter_[_: P]: P[String] =
    P(
      qualifiedName
    ).map(_.mkString)

  private def functionInvocation[_: P]: P[Exp] =
    P(
      (identifierWithWhitespaces
        .map(List(_)) | qualifiedName) ~ "(" ~ functionParameters.? ~ ")"
    ).map {
      case (name :: Nil, None) =>
        FunctionInvocation(name, PositionalFunctionParameters(List.empty))
      case (name :: Nil, Some(parameters)) =>
        FunctionInvocation(name, parameters)
      case (names, None) =>
        QualifiedFunctionInvocation(Ref(names.dropRight(1)),
                                    names.last,
                                    PositionalFunctionParameters(List.empty))
      case (names, Some(parameters)) =>
        QualifiedFunctionInvocation(Ref(names.dropRight(1)),
                                    names.last,
                                    parameters)
    }

  private def functionParameters[_: P]: P[FunctionParameters] =
    namedParameters | positionalParameters

  private def namedParameters[_: P]: P[NamedFunctionParameters] =
    P(
      (parameterName ~ ":" ~ expression).rep(1, sep = ",")
    ).map(params => NamedFunctionParameters(params.toMap))

  private def positionalParameters[_: P]: P[PositionalFunctionParameters] =
    P(
      expression.rep(1, sep = ",")
    ).map(params => PositionalFunctionParameters(params.toList))

  // operators of values that can be chained multiple times (e.g. `a.b.c`, `a[1][2]`, `a.b[1].c`)
  private def chainedValueOp[_: P](value: Exp): P[Exp] =
    (path(value) | filter(value)).flatMap(optional(chainedValueOp))

  private def path[_: P](value: Exp): P[Exp] =
    P(
      ("." ~ (valueProperty | name)).rep(1)
    ).map(ops => ops.foldLeft(value)(PathExpression))

  // list all properties that doesn't match to the regular name (i.e. with whitespaces)
  // - generic parser with whitespace doesn't work because there is no fixed follow-up character
  private def valueProperty[_: P]: P[String] =
    P(
      "time offset"
    ).!

  private def filter[_: P](base: Exp): P[Exp] =
    P(
      ("[" ~ expression ~ "]").rep(1)
    ).map(ops => ops.foldLeft(base)(Filter))

  // --------------- unary-tests expressions ---------------

  private def unaryTests[_: P]: P[Exp] =
    negation | positiveUnaryTests | anyInput

  private def negation[_: P]: P[Exp] =
    P(
      "not" ~ "(" ~ positiveUnaryTests ~ ")"
    ).map(Not)

  private def positiveUnaryTests[_: P]: P[Exp] =
    P(
      positiveUnaryTest.rep(1, sep = ",")
    ).map {
      case test :: Nil => test
      case tests       => AtLeastOne(tests.toList)
    }

  // boolean literals are ambiguous for unary-tests. give precedence to comparison with input.
  private def positiveUnaryTest[_: P]: P[Exp] =
    boolean.map(InputEqualTo) | expression.map(UnaryTestExpression)

  private def anyInput[_: P]: P[Exp] =
    P(
      "-"
    ).map(_ => ConstBool(true))

  private def simplePositiveUnaryTest[_: P]: P[Exp] = unaryComparison | interval

  private def unaryComparison[_: P]: P[Exp] =
    P(
      StringIn("<=", ">=", "<", ">").! ~ endpoint
    ).map {
      case ("<", x)  => InputLessThan(x)
      case ("<=", x) => InputLessOrEqual(x)
      case (">", x)  => InputGreaterThan(x)
      case (">=", x) => InputGreaterOrEqual(x)
    }

  // allow more expressions compared to the spec to align unary-tests with other expression
  private def endpoint[_: P]: P[Exp] = expLvl4

  private def interval[_: P]: P[Exp] =
    P(
      intervalStart ~ ".." ~ intervalEnd
    ).map {
      case (start, end) => Interval(start, end)
    }

  private def intervalStart[_: P]: P[IntervalBoundary] =
    P(
      CharIn("(", "]", "[").! ~ endpoint
    ).map {
      case ("(", x) => OpenIntervalBoundary(x)
      case ("]", x) => OpenIntervalBoundary(x)
      case ("[", x) => ClosedIntervalBoundary(x)
    }

  private def intervalEnd[_: P]: P[IntervalBoundary] =
    P(
      endpoint ~ CharIn(")", "[", "]").!
    ).map {
      case (y, ")") => OpenIntervalBoundary(y)
      case (y, "[") => OpenIntervalBoundary(y)
      case (y, "]") => ClosedIntervalBoundary(y)
    }

  private def rangeBoundary[_: P]: P[Exp] =
    P(
      rangeStart ~ ".." ~ rangeEnd
    ).map {
      case (start, end) => ConstRange(start, end)
    }

  private def rangeStart[_: P]: P[RangeBoundary] =
    P(
      CharIn("(", "]", "[").! ~ expLvl4
    ).map {
      case ("(", x) => OpenRangeBoundary(x)
      case ("]", x) => OpenRangeBoundary(x)
      case ("[", x) => ClosedRangeBoundary(x)
    }

  private def rangeEnd[_: P]: P[RangeBoundary] =
    P(
      expLvl4 ~ CharIn(")", "[", "]").!
    ).map {
      case (y, ")") => OpenRangeBoundary(y)
      case (y, "[") => OpenRangeBoundary(y)
      case (y, "]") => ClosedRangeBoundary(y)
    }

  // --------------- temporal parsers ---------------

  private def parseDate(d: String): Exp = {
    Try(ConstDate(d)).filter(_ => isValidDate(d)).getOrElse(ConstNull)
  }

  private def parseTime(t: String): Exp = {
    Try {
      if (isOffsetTime(t)) {
        ConstTime(t)
      } else {
        ConstLocalTime(t)
      }
    }.getOrElse(ConstNull)
  }

  private def parseDateTime(dt: String): Exp = {
    Try {
      if (isValidDate(dt)) {
        ConstLocalDateTime((dt: Date).atTime(0, 0))
      } else if (isOffsetDateTime(dt)) {
        ConstDateTime(dt)
      } else {
        ConstLocalDateTime(dt)
      }
    }.getOrElse(ConstNull)
  }

  private def parseDuration(d: String): Exp = {
    Try {
      if (isYearMonthDuration(d)) {
        ConstYearMonthDuration(d)
      } else {
        ConstDayTimeDuration(d)
      }
    }.getOrElse(ConstNull)
  }
}
