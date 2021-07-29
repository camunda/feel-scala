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

import fastparse.JavaWhitespace._
import fastparse._
import org.camunda.feel._
import org.camunda.feel.syntaxtree._

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

  private def fullExpression[_: P] = P(Start ~ expression ~ End)

  private def fullUnaryExpression[_: P] = P(Start ~ unaryTests ~ End)

  private def reservedWord[_: P] =
    P(
      StringIn("null",
        "true",
        "false",
        "function",
        "if",
        "then",
        "else",
        "for",
        "between",
        "instance",
        "of",
        "some",
        "every"))

  // list of built-in function names with whitespaces
  // -- other names match the 'function name' pattern
  private def builtinFunctionName[_: P] =
    P(
      StringIn(
        "date and time",
        "years and months duration",
        "string length",
        "upper case",
        "lower case",
        "substring before",
        "substring after",
        "starts with",
        "ends with",
        "list contains",
        "insert before",
        "index of",
        "distinct values",
        "get entries",
        "get value",
        "is defined",
        "day of year",
        "day of week",
        "month of year",
        "week of year",
        "put all"
      )
    )

  // list of built-in function parameter names with whitespaces
  // -- other names match the 'parameter name' pattern
  private def builtinFunctionParameterNames[_: P] =
    P(StringIn("start position", "grouping separator", "decimal separator"))

  // an identifier which is not a reserved word. however, it can contain a reserved word.
  private def identifier[_: P] = P(reservedWord.? ~~ javaLikeIdentifier)

  private def javaLikeIdentifier[_: P] =
    P(
      CharPred(Character.isJavaIdentifierStart) ~~ CharsWhile(
        Character.isJavaIdentifierPart,
        0)
    )

  // characters or string escape sequences (\', \", \\, \n, \r, \t, \u269D, \U101EF)
  private def stringLiteralWithQuotes[_: P]: P[String] =
    P("\"" ~~ (("\\" | !"\"") ~~ AnyChar).repX.! ~~ "\"")

  private def expression[_: P]: P[Exp] = P(textualExpression)

  private def expression10[_: P] = P(boxedExpression)

  private def textualExpressions[_: P]: P[ConstList] =
    P(textualExpression.rep(1, sep = ",").map(s => ConstList(s.toList)))

  private def textualExpression[_: P]: P[Exp] =
    P(expressionWithContinuation | functionDefinition | forExpression | ifExpression | quantifiedExpression | expression2)

  private def expressionWithContinuation[_: P]: P[Exp] =
    P(
      "(" ~ expression ~ ")" |
        variableExpression |
        list |
        context |
        functionInvocation
    ).flatMap(continuation)

  // optimize parsing the expression from left to right by using `flapMap()`
  // `flatMap()` takes the parsed (left) part of the expression and continues with the (right) part
  // it does backtracking if `flatMap()` is not successful
  private def continuation[_: P](base: Exp): P[Exp] = P(
    continuationExpression(base).flatMap(continuation) |
      End.map(_ => base)
  )

  private def continuationExpression[_: P](base: Exp): P[Exp] =
    filterContinuation(base) | pathContinuation(base)

  private def filterContinuation[_: P](base: Exp): P[Exp] =
    P("[" ~ expression ~ "]")
      .map(filter => Filter(base, filter))

  private def pathContinuation[_: P](base: Exp): P[Exp] =
    P(("." ~ name).rep(1))
      .map(ops => ops.foldLeft(base)(PathExpression))

  // an (escaped) identifier but not the name of a function invocation or path expression
  private def variableExpression[_: P]: P[Exp] =
    P((identifier.! | escapedIdentifier) ~ !"(").map(n => Ref(List(n)))

  // 2b)
  private def expression2[_: P]: P[Exp] = P(disjunction)

  private def expression3[_: P]: P[Exp] = P(conjunction)

  private def expression4[_: P]: P[Exp] =
    P(expression5.flatMap(x =>
      comparison(x).?.map(_.fold(x)(comparisonExpr => comparisonExpr))))

  private def comparison[_: P](x: Exp): P[Exp] = {
    (StringIn("<=", ">=", "<", ">", "!=", "=").! ~ expression5).map {
      case ("=", y)  => Equal(x, y)
      case ("!=", y) => Not(Equal(x, y))
      case ("<", y)  => LessThan(x, y)
      case ("<=", y) => LessOrEqual(x, y)
      case (">", y)  => GreaterThan(x, y)
      case (">=", y) => GreaterOrEqual(x, y)
    } |
      ("between" ~ expression5 ~ "and" ~ expression5).map {
        case (a, b) => Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b))
      } |
      ("in" ~ "(" ~ positiveUnaryTests ~ ")").map(y => In(x, y)) |
      ("in" ~ positiveUnaryTest).map(y => In(x, y))
  }

  private def expression5[_: P] = P(arithmeticExpression)

  private def expression6[_: P] =
    P(expression7.flatMap(x =>
      instanceOf.?.map(_.fold(x)(typeName => InstanceOf(x, typeName)))))

  private def instanceOf[_: P]: P[String] =
    P("instance" ~ "of" ~/ typeName)

  private def typeName[_: P]: P[String] =
    P(qualifiedName.map(_.mkString(".")))

  private def expression7[_: P] = P(pathExpression)

  private def expression8[_: P] =
    P(functionInvocation | filteredExpression9)

  private def expression9[_: P] =
    P(
      (!dateTimeLiteral ~ name.map(n => Ref(List(n)))) |
        literal |
        inputValueSymbol |
        simplePositiveUnaryTest |
        "(" ~ textualExpression ~ ")" |
        expression10
    )

  private def inputValueSymbol[_: P] = P("?").map(_ => ConstInputValue)

  private def simpleExpressions[_: P]: P[ConstList] =
    P[ConstList](
      simpleExpression.rep(1, sep = ",").map(s => ConstList(s.toList)))

  private def simpleExpression[_: P]: P[Exp] =
    P[Exp](arithmeticExpression | simpleValue)

  private def arithmeticExpression[_: P] =
    P(arithmeticExpression2 ~ (CharIn("+\\-").! ~ arithmeticExpression2).rep)
      .map {
        case (base, ops) =>
          ops.foldLeft(base) {
            case (left, (op, right)) =>
              op match {
                case "+" => Addition(left, right)
                case "-" => Subtraction(left, right)
              }
          }
      }

  private def arithmeticExpression2[_: P] =
    P(arithmeticExpression3 ~ (CharIn("*/").! ~ arithmeticExpression3).rep)
      .map {
        case (base, ops) =>
          ops.foldLeft(base) {
            case (left, (op, right)) =>
              op match {
                case "*" => Multiplication(left, right)
                case "/" => Division(left, right)
              }
          }
      }

  private def arithmeticExpression3[_: P] =
    P(arithmeticExpression4 ~ ("**" ~ arithmeticExpression4).rep)
      .map { case (base, ops) => ops.foldLeft(base)(Exponentiation) }

  private def arithmeticExpression4[_: P] = P("-".!.? ~ expression6).map {
    case (Some("-"), exp) => ArithmeticNegation(exp)
    case (_, exp)         => exp
  }

  private def unaryTests[_: P]: P[Exp] = P(
    acceptAnyInputValue |
      ("not" ~ "(" ~ positiveUnaryTests ~ ")").map(Not) |
      positiveUnaryTests
  )

  private def acceptAnyInputValue[_: P]() =
    P("-" ~ End).map(_ => ConstBool(true))

  private def positiveUnaryTests[_: P]: P[Exp] =
    P(positiveUnaryTest.rep(1, ",").map {
      case Seq(test) => test
      case tests     => AtLeastOne(tests.toList)
    })

  // in DMN 1.2 it's only 'expression' (which also covers simple positive unary test)
  //    - however, parse simple positive unary test first since this is most usual
  private def positiveUnaryTest[_: P]: P[Exp] =
    P(
      P("null").map(_ => InputEqualTo(ConstNull)) |
        simplePositiveUnaryTest ~ End |
        expression.map(UnaryTestExpression)
    )

  private def simpleUnaryTests[_: P]: P[Exp] = P[Exp](
    acceptAnyInputValue |
      ("not" ~ "(" ~ simplePositiveUnaryTests ~ ")").map(Not) |
      simplePositiveUnaryTests
  )

  private def simplePositiveUnaryTests[_: P]: P[Exp] =
    P(simplePositiveUnaryTest.rep(1, ",").map {
      case test :: Nil => test
      case tests       => AtLeastOne(tests.toList)
    })

  private def simplePositiveUnaryTest[_: P]: P[Exp] =
    P(
      "<" ~ endpoint.map(InputLessThan) |
        "<=" ~ endpoint.map(InputLessOrEqual) |
        ">" ~ endpoint.map(InputGreaterThan) |
        ">=" ~ endpoint.map(InputGreaterOrEqual) |
        interval |
        simpleValue.map(InputEqualTo)
    )

  // allow any expression as endpoint
  private def endpoint[_: P]: P[Exp] = P(simpleValue | expression)

  // need to exclude function invocation from qualified name
  private def simpleValue[_: P] =
    P(
      simpleLiteral |
        !functionInvocation ~ qualifiedName.map(Ref(_)) |
        inputValueSymbol
    )

  private def literal[_: P]: P[Exp] =
    P(P("null").map(_ => ConstNull) | simpleLiteral)

  private def simpleLiteral[_: P] =
    P(booleanLiteral | dateTimeLiteral | stringLiteral | numericLiteral)

  private def booleanLiteral[_: P]: P[ConstBool] =
    P(P("true").map(_ => ConstBool(true)) | P("false").map(_ =>
      ConstBool(false)))

  private def dateTimeLiteral[_: P]: P[Exp] =
    P(
      ("date" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseDate) |
        ("time" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseTime) |
        ("date and time" ~ "(" ~ stringLiteralWithQuotes ~ ")")
          .map(parseDateTime) |
        ("duration" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseDuration)
    ).opaque("expected date time literal")

  private def stringLiteral[_: P]: P[ConstString] =
    P(stringLiteralWithQuotes.map(ConstString))

  private def numericLiteral[_: P]: P[ConstNumber] =
    P(CharIn("\\-").? ~~ (integral ~~ fractional.?) | fractional).!.map(
      number => ConstNumber(BigDecimal(number))
    )

  private def integral[_: P] = P("0" | CharIn("1-9") ~~ digits.?)

  private def fractional[_: P] = P("." ~~ digits)

  private def digits[_: P] = P(CharsWhileIn("0-9"))

  private def digit[_: P] = P(CharIn("0-9"))

  private def qualifiedName[_: P]: P[List[String]] =
    P(name.repX(1, sep = ".").map(_.toList))

  // simplified name definition
  private def name[_: P]: P[String] =
    P(escapedIdentifier | "time offset".! | identifier.!)

  private def escapedIdentifier[_: P]: P[String] =
    P("`" ~~ (!"`" ~~ AnyChar.!).repX(1) ~~ "`")
      .map(_.mkString)

  // FEEL name definition
  private def feelName[_: P]: P[String] =
    P((nameStart ~ (namePart | additionalNameSymbols).rep.!).map {
      case (s, ps) =>
        s + ps.mkString
    })

  private def nameStart[_: P] =
    P((nameStartChar ~ namePartChar.rep).map {
      case (s, ps) => s + ps.mkString
    })

  private def namePart[_: P]: P[String] = P(namePartChar.rep(1).map(_.mkString))

  private def nameStartChar[_: P]: P[String] = P(
    (
      "?" | CharIn("A-Z") | "_" | CharIn("a-z") |
        CharIn("\u200C-\u200D") | CharIn("\u2070-\u218F") | CharIn(
        "\u2C00-\u2FEF") | CharIn("\u3001-\uD7FF") | CharIn(
        "\uF900-\uFDCF"
      ) | CharIn("\uFDF0-\uFFFD") | CharIn("\u10000-\uEFFFF")
    ).!
  )

  private def namePartChar[_: P] =
    P(nameStartChar | digit | CharIn("\u0300-\u036F") | CharIn("\u203F-\u2040"))

  private def additionalNameSymbols[_: P] = P("." | "/" | "-" | "’" | "+" | "*")

  private def interval[_: P]: P[Interval] =
    P(intervalStart.! ~ endpoint ~ ".." ~/ endpoint ~/ intervalEnd.!)
      .map {
        case ("(" | "]", start, end, ")" | "[") =>
          syntaxtree.Interval(OpenIntervalBoundary(start),
                              OpenIntervalBoundary(end))
        case ("(" | "]", start, end, "]") =>
          syntaxtree.Interval(OpenIntervalBoundary(start),
                              ClosedIntervalBoundary(end))
        case ("[", start, end, ")" | "[") =>
          syntaxtree.Interval(ClosedIntervalBoundary(start),
                              OpenIntervalBoundary(end))
        case ("[", start, end, "]") =>
          syntaxtree.Interval(ClosedIntervalBoundary(start),
                              ClosedIntervalBoundary(end))
      }

  private def intervalStart[_: P] = P(CharIn("(", "]", "["))

  private def intervalEnd[_: P] = P(CharIn(")", "[", "]"))

  private def forExpression[_: P]: P[For] =
    P(("for" ~ listIterator.rep(1, sep = ",") ~/ "return" ~/ expression).map {
      case (iterators, exp) =>
        syntaxtree.For(iterators.toList, exp)
    })

  private def listIterator[_: P] = P(name ~ "in" ~/ (range | expression))

  private def range[_: P]: P[syntaxtree.Range] =
    P((expression ~ ".." ~ expression).map {
      case (start, end) =>
        syntaxtree.Range(start, end)
    })

  private def ifExpression[_: P]: P[If] =
    P(("if" ~ expression ~ "then" ~ expression ~ "else" ~ expression).map {
      case (condition, statement, elseStatement) =>
        If(condition, statement, elseStatement)
    })

  private def quantifiedExpression[_: P]: P[Exp] =
    P(
      (("some" | "every").! ~ listIterator
        .rep(1, sep = ",") ~ "satisfies" ~/ expression).map {
        case ("some", iterators, condition) =>
          SomeItem(iterators.toList, condition)
        case ("every", iterators, condition) =>
          EveryItem(iterators.toList, condition)
      })

  private def disjunction[_: P]: P[Exp] =
    P(expression3 ~ ("or" ~ expression3).rep)
      .map { case (base, ops) => ops.foldLeft(base)(Disjunction) }

  private def conjunction[_: P]: P[Exp] =
    P(expression4 ~ ("and" ~ expression4).rep)
      .map { case (base, ops) => ops.foldLeft(base)(Conjunction) }

  // allow nested path expressions
  private def pathExpression[_: P]: P[Exp] =
    P(expression8 ~ ("." ~ name).rep ~ ("[" ~ expression ~ "]").?)
      .map {
        case (base, ops, None) => ops.foldLeft(base)(PathExpression)
        case (base, ops, Some(filter)) =>
          val pathExpression = ops.foldLeft(base)(PathExpression)
          Filter(pathExpression, filter)
      }

  private def filteredExpression9[_: P]: P[Exp] =
    P(expression9.flatMap(x =>
      ("[" ~ expression ~ "]").?.map(_.fold(x)(filterExp =>
        Filter(x, filterExp)))))

  private def functionInvocation[_: P]: P[Exp] =
    P(!dateTimeLiteral ~ (builtinFunctionName.!.map(List(_)) | qualifiedName) ~ parameters)
      .map {
        case (names, params) =>
          names match {
            case name :: Nil => FunctionInvocation(name, params)
            case _ =>
              val path = Ref(names.dropRight(1))
              QualifiedFunctionInvocation(path, names.last, params)
          }
      }

  private def parameters[_: P]: P[FunctionParameters] = P(
    P("(" ~ ")").map(_ => PositionalFunctionParameters(List())) |
      "(" ~ (namedParameters | positionalParameters) ~ ")"
  )

  private def namedParameters[_: P] =
    P(
      namedParameter
        .rep(1, sep = ",")
        .map(p => NamedFunctionParameters(p.toMap)))

  private def namedParameter[_: P] = P(parameterName ~ ":" ~/ expression)

  // should be FEEL name
  private def parameterName[_: P] = P(builtinFunctionParameterNames.! | name)

  private def positionalParameters[_: P] = P(
    expression
      .rep(1, sep = ",")
      .map(s => PositionalFunctionParameters(s.toList))
  )

  private def boxedExpression[_: P]: P[Exp] =
    P(list | functionDefinition | context)

  private def list[_: P]: P[ConstList] =
    P(("[" ~ expression7.rep(0, sep = ",") ~ "]").map(s => ConstList(s.toList)))

  private def functionDefinition[_: P]: P[FunctionDefinition] =
    P(
      ("function" ~ "(" ~ formalParameter
        .rep(0, sep = ",") ~/ ")" ~/ (externalJavaFunction | expression)).map {
        case (params, body) => FunctionDefinition(params.toList, body)
      }
    )

  private def externalJavaFunction[_: P]: P[JavaFunctionInvocation] = P(
    ("external" ~ "{" ~ "java" ~ ":" ~ "{" ~ functionClassName ~ "," ~ functionMethodSignature ~ "}" ~ "}")
      .map {
        case (className, (methodName, arguments)) =>
          JavaFunctionInvocation(className, methodName, arguments.toList)
      }
  )

  private def functionClassName[_: P] =
    P("class" ~/ ":" ~ stringLiteralWithQuotes)

  private def functionMethodSignature[_: P] = P(
    "method signature" ~/ ":" ~/ "\"" ~ name ~/ "(" ~ functionMethodArgument
      .rep(0, sep = ",") ~ ")" ~/ "\""
  )

  private def functionMethodArgument[_: P] =
    P(qualifiedName.map(_.mkString(".")))

  private def formalParameter[_: P] = P(parameterName)

  private def context[_: P]: P[ConstContext] =
    P(("{" ~ contextEntry.rep(0, sep = ",") ~ "}").map(s =>
      ConstContext(s.toList)))

  private def contextEntry[_: P]: P[(String, Exp)] = P(key ~ ":" ~ expression)

  private def key[_: P] = P(name | stringLiteralWithQuotes)

  private def parseDate(d: String): Exp = {
    Try(ConstDate(d)).filter(_ => isValidDate(d)).getOrElse(ConstNull)
  }

  private def parseTime(t: String): Exp = {
    if (isOffsetTime(t)) {
      Try(ConstTime(t)).getOrElse(ConstNull)
    } else {
      Try(ConstLocalTime(t)).getOrElse(ConstNull)
    }
  }

  private def parseDateTime(dt: String): Exp = {
    if (isValidDate(dt)) {
      Try(ConstLocalDateTime((dt: Date).atTime(0, 0))).getOrElse(ConstNull)
    } else if (isOffsetDateTime(dt)) {
      Try(ConstDateTime(dt)).getOrElse(ConstNull)
    } else if (isLocalDateTime(dt)) {
      Try(ConstLocalDateTime(dt)).getOrElse(ConstNull)
    } else {
      ConstNull
    }
  }

  private def parseDuration(d: String): Exp = {
    if (isYearMonthDuration(d)) {
      Try(ConstYearMonthDuration(d)).getOrElse(ConstNull)
    } else if (isDayTimeDuration(d)) {
      Try(ConstDayTimeDuration(d)).getOrElse(ConstNull)
    } else {
      ConstNull
    }
  }
}
