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
object FeelParser extends JavaTokenParsers {

  def parseExpression(exp: String): Parsed[Exp] =
    parse(exp, fullExpression(_))
  private def fullExpression[_: P] = "" ~ expression ~ End
  def parseUnaryTests(expression: String): Parsed[Exp] =
    parse(expression, fullUnaryExpression(_))
  private def fullUnaryExpression[_: P] = P("" ~ unaryTests ~ End)

  private def reservedWord[_: P]: P[String] =
    P[String](
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
               "of").!)

  // list of built-in function names with whitespaces
  // -- other names match the 'function name' pattern
  private def builtinFunctionName[_: P]: P[String] = P[String](
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
    ).!
  )

  // list of built-in function parameter names with whitespaces
  // -- other names match the 'parameter name' pattern
  private def builtinFunctionParameterNames[_: P]: P[String] =
    P[String](
      StringIn("start position", "grouping separator", "decimal separator").!)

  private def identifier[_: P]: P[String] = P(!(reservedWord ~~ !ident) ~ ident)

  // Java-like string literal: ("\""+"""([^"\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*"""+"\"")
  // modification: allow '\'
  private def stringLiteralWithQuotes[_: P]: P[String] =
    //P[String](""""([^"\x00-\x1F\x7F]|\\u[a-fA-F0-9]{4})*"""".r.map(_.replaceAll("\"", "")))
    P[String]("\"" ~~ CharsWhile(_ != '"', 0).! ~~ "\"")

  // 1 a)
  private def expression[_: P]: P[Exp] = P[Exp](textualExpression)
  // 1 b)
  private def expression10[_: P] = P(boxedExpression)

  // 3
  private def textualExpressions[_: P]: P[ConstList] =
    P[ConstList](
      textualExpression.rep(1, sep = ",").map(s => ConstList(s.toList)))

  // 2 a)
  private def textualExpression[_: P]: P[Exp] =
    P[Exp](
      functionDefinition | forExpression | ifExpression | quantifiedExpression | expression2)
  // 2b)
  private def expression2[_: P] = P(disjunction)
  // 2 c)
  private def expression3[_: P] = P(conjunction)
  // 2 d)
  private def expression4[_: P]: P[Exp] =
    P[Exp](expression5.flatMap(optionalComparison))
  // 2 e)
  private def expression5[_: P] = P(arithmeticExpression)
  // 2 f)
  private def expression6[_: P] =
    P(expression7.flatMap(x => instanceOf.?.map(_.fold(x)(InstanceOf(x, _)))))
  // 2 g)
  private def expression7[_: P] = P(pathExpression)
  // 2 h)
  private def expression8[_: P] =
    P(functionInvocation | builtinFunctionInvocation | filteredExpression9)
  // 2 i)
  private def expression9[_: P] =
    P(
      (!dateTimeLiteral ~ name.map(n => Ref(List(n)))) |
        literal |
        qmark |
        simplePositiveUnaryTest |
        "(" ~ textualExpression ~ ")" |
        expression10
    )

  private def qmark[_: P] = P("?") ^^^ ConstInputValue
  // 6
  private def simpleExpressions[_: P]: P[ConstList] =
    P[ConstList](
      simpleExpression.rep(1, sep = ",").map(s => ConstList(s.toList)))

  // 5
  private def simpleExpression[_: P]: P[Exp] =
    P[Exp](arithmeticExpression | simpleValue)

  // 4 a) -> 21+22
  private def arithmeticExpression[_: P] = P(
    chainl1(arithmeticExpression2, P("+") ^^^ Addition | P("-") ^^^ Subtraction)
  )
  // 4 b) -> 23+24
  private def arithmeticExpression2[_: P] = P(
    chainl1(arithmeticExpression3,
            P("*") ^^^ Multiplication | P("/") ^^^ Division)
  )
  // 4 c) -> 25
  private def arithmeticExpression3[_: P] =
    P(chainl1(arithmeticExpression4, P("**") ^^^ Exponentiation))
  // 4 d) -> 26
  private def arithmeticExpression4[_: P] =
    P(("-".!.? ~ expression6).map {
      case (Some(_), e) => ArithmeticNegation(e)
      case (None, e)    => e
    })

  // 17
  private def unaryTests[_: P]: P[Exp] = P[Exp](
    ("not" ~ "(" ~ positiveUnaryTests ~ ")").map(Not) |
      positiveUnaryTests |
      P("-") ^^^ ConstBool(true)
  )

  // 16
  private def positiveUnaryTests[_: P]: P[Exp] =
    P[Exp](positiveUnaryTest.rep(1, ",").map {
      case Seq(test) => test
      case tests     => AtLeastOne(tests.toList)
    })

  // 15 - in DMN 1.2 it's only 'expression' (which also covers simple positive unary test)
  //    - however, parse simple positive unary test first since this is most usual
  private def positiveUnaryTest[_: P]: P[Exp] =
    P[Exp](
      P("null") ^^^ InputEqualTo(ConstNull) |
        simplePositiveUnaryTest |
        expression.map(UnaryTestExpression)
    )

  // 14
  private def simpleUnaryTests[_: P]: P[Exp] = P[Exp](
    P("-") ^^^ ConstBool(true) |
      ("not" ~ "(" ~ simplePositiveUnaryTests ~ ")").map(Not) |
      simplePositiveUnaryTests
  )

  // 13
  private def simplePositiveUnaryTests[_: P]: P[Exp] =
    P[Exp](simplePositiveUnaryTest.rep(1, ",").map {
      case test :: Nil => test
      case tests       => AtLeastOne(tests.toList)
    })

  // 7
  private def simplePositiveUnaryTest[_: P]: P[Exp] =
    P[Exp](
      "<" ~ endpoint.map(InputLessThan) |
        "<=" ~ endpoint.map(InputLessOrEqual) |
        ">" ~ endpoint.map(InputGreaterThan) |
        ">=" ~ endpoint.map(InputGreaterOrEqual) |
        interval |
        simpleValue.map(InputEqualTo)
    )

  // 18 - allow any expression as endpoint
  private def endpoint[_: P]: P[Exp] = P[Exp](simpleValue | expression)

  // 19 - need to exclude function invocation from qualified name
  private def simpleValue[_: P] =
    P(
      simpleLiteral |
        !(functionInvocation | builtinFunctionInvocation) ~ qualifiedName.map(
          Ref(_)) |
        qmark
    )
  // 33
  private def literal[_: P]: P[Exp] =
    P[Exp](P("null") ^^^ ConstNull | simpleLiteral)

  // 34
  private def simpleLiteral[_: P] =
    P(booleanLiteral | dateTimeLiteral | stringLiteral | numericLiteral)

  // 36
  private def booleanLiteral[_: P]: P[ConstBool] =
    P[ConstBool](
      P("true") ^^^ ConstBool(true) | P("false") ^^^ ConstBool(false))

  // 62
  private def dateTimeLiteral[_: P]: P[Exp] =
    P[Exp](
      ("date" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseDate) |
        ("time" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseTime) |
        ("date and time" ~ "(" ~ stringLiteralWithQuotes ~ ")")
          .map(parseDateTime) |
        ("duration" ~ "(" ~ stringLiteralWithQuotes ~ ")").map(parseDuration)
    ).opaque("expected date time literal")

  // 35 -
  private def stringLiteral[_: P]: P[ConstString] =
    P[ConstString](stringLiteralWithQuotes.map(ConstString))

  // 37 - use combined regex instead of multiple parsers
  private def numericLiteral[_: P]: P[ConstNumber] =
    P[ConstNumber]((wholeNumber ~ ("." ~ digits()).?).map {
      case (p1, p2) =>
        ConstNumber(BigDecimal(s"$p1${p2.map("." + _).getOrElse("")}"))
    })

  // 39
  private def digits[_: P](min: Int = 1): P[String] =
    P[String](digit.rep(min).map(_.mkString))

  // 38
  private def digit[_: P]: P[String] = P[String](CharIn("0-9").!)

  // 20
  private def qualifiedName[_: P]: P[List[String]] =
    P[List[String]](name.repX(1, sep = ".").map(_.toList))

  // 27 - simplified name definition
  private def name[_: P]: P[String] =
    P[String](escapedIdentifier | "time offset".! | identifier)

  private def escapedIdentifier[_: P]: P[String] = P[String](
    //("`" ~ """([^`"\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*""".r ~ "`")
    "`" ~ CharsWhile(_ != '`').! ~ "`"
  )

  // FEEL name definition
  private def feelName[_: P]: P[String] =
    P[String]((nameStart ~ (namePart | additionalNameSymbols).rep.!).map {
      case (s, ps) =>
        s + ps.mkString
    })

  // 28
  private def nameStart[_: P] =
    P[String]((nameStartChar ~ namePartChar.rep).map {
      case (s, ps) =>
        s + ps.mkString
    })

  // 29
  private def namePart[_: P]: P[String] = P(namePartChar.rep(1).map(_.mkString))

  // 30- unknown unicode chars CharIn("\\uC0-\\uD6") | CharIn("\\uD8-\\uF6") | CharIn("\\uF8-\\u2FF") | CharIn("\\u370-\\u37D") | CharIn("\\u37F-\u1FFF")
  private def nameStartChar[_: P]: P[String] = P(
    (
      "?" | CharIn("A-Z") | "_" | CharIn("a-z") |
        CharIn("\u200C-\u200D") | CharIn("\u2070-\u218F") | CharIn(
        "\u2C00-\u2FEF") | CharIn("\u3001-\uD7FF") | CharIn(
        "\uF900-\uFDCF"
      ) | CharIn("\uFDF0-\uFFFD") | CharIn("\u10000-\uEFFFF")
    ).!
  )

  // 31 - unknown unicode char "[\\uB7]".r
  private def namePartChar[_: P] =
    P(nameStartChar | digit | CharIn("\u0300-\u036F") | CharIn("\u203F-\u2040"))

  // 32
  private def additionalNameSymbols[_: P] = P("." | "/" | "-" | "’" | "+" | "*")

  // 8
  private def interval[_: P]: P[Interval] =
    P[Interval](
      ((openIntervalStart | closedIntervalStart) ~ endpoint ~ ".." ~/ endpoint ~/ (openIntervalEnd | closedIntervalEnd))
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
    )

  // 9
  private def openIntervalStart[_: P]: P[String] = P(("(" | "]").!)

  // 10
  private def closedIntervalStart[_: P]: P[String] = P("[".!)

  // 11
  private def openIntervalEnd[_: P]: P[String] = P((")" | "[").!)

  // 12
  private def closedIntervalEnd[_: P]: P[String] = P("]".!)

  // 46
  private def forExpression[_: P]: P[For] =
    P[For](
      ("for" ~ listIterator.rep(1, sep = ",") ~/ "return" ~/ expression).map {
        case (iterators, exp) =>
          syntaxtree.For(iterators.toList, exp)
      })

  private def listIterator[_: P] = P(name ~ "in" ~/ (range | expression))

  private def range[_: P]: P[syntaxtree.Range] =
    P[syntaxtree.Range]((expression ~ ".." ~ expression).map {
      case (start, end) =>
        syntaxtree.Range(start, end)
    })

  // 47
  private def ifExpression[_: P]: P[If] =
    P[If](("if" ~ expression ~ "then" ~ expression ~ "else" ~ expression).map {
      case (condition, statement, elseStatement) =>
        If(condition, statement, elseStatement)
    })

  // 48 - no separator in spec grammar but in examples
  private def quantifiedExpression[_: P]: P[Exp] =
    P[Exp](
      (("some" | "every").! ~ listIterator
        .rep(1, sep = ",") ~ "satisfies" ~/ expression).map {
        case ("some", iterators, condition) =>
          SomeItem(iterators.toList, condition)
        case ("every", iterators, condition) =>
          EveryItem(iterators.toList, condition)
      })

  /** A parser generator that, roughly, generalises the rep1sep generator so
    * that `q`, which parses the separator, produces a left-associative
    * function that combines the elements it separates.
    *
    * ''From: J. Fokker. Functional parsers. In J. Jeuring and E. Meijer, editors, Advanced Functional Programming,
    * volume 925 of Lecture Notes in Computer Science, pages 1--23. Springer, 1995.''
    *
    * @param p a parser that parses the elements
    * @param q a parser that parses the token(s) separating the elements, yielding a left-associative function that
    *          combines two elements into one
    * def chainl1[T](p: => Parser[T], q: => Parser[(T, T) => T]): Parser[T]
    */
  def chainl1[_: P, T](p: => P[T], q: => P[(T, T) => T]): P[T] =
    chainl1(p, p, q)

  /** A parser generator that, roughly, generalises the `rep1sep` generator
    * so that `q`, which parses the separator, produces a left-associative
    * function that combines the elements it separates.
    *
    * @param first a parser that parses the first element
    * @param p     a parser that parses the subsequent elements
    * @param q     a parser that parses the token(s) separating the elements,
    *              yielding a left-associative function that combines two elements
    *              into one
    */
  def chainl1[_: P, T, U](first: => P[T],
                          p: => P[U],
                          q: => P[(T, U) => T]): P[T] =
    (first ~ (q ~ p).rep).map {
      case (x, xs) =>
        xs.foldLeft(x: T) {
          case (a, (f, b)) =>
            f(a, b)
        } // x's type annotation is needed to deal with changed type inference due to SI-5189
    }
  implicit class RichParser[_: P, T](p: P[T]) {
    def ^^^[V](v: V): P[V] = p.map(_ => v)
  }
  // 49
  private def disjunction[_: P]: P[Exp] =
    P[Exp](chainl1(expression3, P("or") ^^^ Disjunction))

  // 50
  private def conjunction[_: P]: P[Exp] =
    P[Exp](chainl1(expression4, P("and") ^^^ Conjunction))

  // 51
  private def optionalComparison[_: P]: Exp => P[Exp] =
    (x: Exp) =>
      P(
        simpleComparison(x) |
          ("between" ~ expression5 ~ "and" ~ expression5).map {
            case (a, b) =>
              Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b))
          } |
          ("in" ~ "(" ~ positiveUnaryTests ~ ")").map(tests => In(x, tests)) |
          ("in" ~ positiveUnaryTest).map(test => In(x, test)) |
          Pass(x)
    )

  private def simpleComparison[_: P](x: Exp): P[Exp] =
    P((StringIn("<=", ">=", "<", ">", "!=", "=").! ~ expression5).map {
      case ("=", y)  => Equal(x, y)
      case ("!=", y) => Not(Equal(x, y))
      case ("<", y)  => LessThan(x, y)
      case ("<=", y) => LessOrEqual(x, y)
      case (">", y)  => GreaterThan(x, y)
      case (">=", y) => GreaterOrEqual(x, y)
    })

  // 53
  private def instanceOf[_: P]: P[String] =
    P[String]("instance" ~ "of" ~/ typeName)

  // 54
  private def typeName[_: P]: P[String] =
    P[String](qualifiedName.map(_.mkString(".")))

  // 45 - allow nested path expressions
  private def pathExpression[_: P]: P[Exp] =
    P[Exp](
      (chainl1(expression8, name, P(".") ^^^ PathExpression) ~ ("[" ~ expression ~ "]").?)
        .map {
          case (path, None)         => path
          case (path, Some(filter)) => Filter(path, filter)
        })

  // 52
  private def theFilter[_: P]: P[Exp] = P("[" ~/ expression ~ "]")
  private def filteredExpression9[_: P]: P[Exp] =
    P[Exp]((expression9 ~ theFilter.?).map {
      case (list, Some(filter)) => Filter(list, filter)
      case (list, None)         => list
    })

  // 40
  private def functionInvocation[_: P]: P[Exp] =
    P[Exp]((!dateTimeLiteral ~ qualifiedName ~ parameters).map {
      case (names, params) =>
        names match {
          case name :: Nil => FunctionInvocation(name, params)
          case _ =>
            QualifiedFunctionInvocation(Ref(names.dropRight(1)),
                                        names.last,
                                        params)
        }
    })

  private def builtinFunctionInvocation[_: P]: P[Exp] =
    P[Exp]((!dateTimeLiteral ~ builtinFunctionName ~ parameters).map {
      case (name, params) =>
        syntaxtree.FunctionInvocation(name, params)
    })

  // 41
  private def parameters[_: P]: P[FunctionParameters] = P[FunctionParameters](
    P("(" ~ ")") ^^^ PositionalFunctionParameters(List()) |
      "(" ~ (namedParameters | positionalParameters) ~ ")"
  )

  // 42
  private def namedParameters[_: P] =
    P(
      namedParameter
        .rep(1, sep = ",")
        .map(p => NamedFunctionParameters(p.toMap)))

  private def namedParameter[_: P] = P(parameterName ~ ":" ~/ expression)

  // 43 - should be FEEL name
  private def parameterName[_: P] = P(builtinFunctionParameterNames | name)

  // 44
  private def positionalParameters[_: P] = P(
    expression
      .rep(1, sep = ",")
      .map(s => PositionalFunctionParameters(s.toList))
  )

  // 55
  private def boxedExpression[_: P]: P[Exp] =
    P[Exp](list | functionDefinition | context)

  // 56
  private def list[_: P]: P[ConstList] =
    P[ConstList](
      ("[" ~ expression7.rep(0, sep = ",") ~ "]").map(s => ConstList(s.toList)))

  // 57
  private def functionDefinition[_: P]: P[FunctionDefinition] =
    P[FunctionDefinition](
      ("function" ~ "(" ~ formalParameter
        .rep(0, sep = ",") ~/ ")" ~/ (externalJavaFunction | expression)).map {
        case (params, body) => FunctionDefinition(params.toList, body)
      }
    )

  private def externalJavaFunction[_: P]
    : P[JavaFunctionInvocation] = P[JavaFunctionInvocation](
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

  // 58
  private def formalParameter[_: P] = P(parameterName)

  // 59
  private def context[_: P]: P[ConstContext] =
    P[ConstContext](("{" ~ contextEntry.rep(0, sep = ",") ~ "}").map(s =>
      ConstContext(s.toList)))

  // 60
  private def contextEntry[_: P]: P[(String, Exp)] = P(key ~ ":" ~ expression)

  // 61
  private def key[_: P] = P(name | stringLiteralWithQuotes)

  private def parseDate(d: String): Exp = {
    if (isValidDate(d)) {
      Try(ConstDate(d)).getOrElse {
        logger.warn(s"Failed to parse date from '$d'");
        ConstNull
      }
    } else {
      logger.warn(s"Failed to parse date from '$d'");
      ConstNull
    }
  }

  private def parseTime(t: String): Exp = {
    if (isOffsetTime(t)) {
      Try(ConstTime(t)).getOrElse {
        logger.warn(s"Failed to parse time from '$t'");
        ConstNull
      }
    } else {
      Try(ConstLocalTime(t)).getOrElse {
        logger.warn(s"Failed to parse local-time from '$t'");
        ConstNull
      }
    }
  }

  private def parseDateTime(dt: String): Exp = {
    if (isValidDate(dt)) {
      Try(ConstLocalDateTime((dt: Date).atTime(0, 0))).getOrElse {
        logger.warn(s"Failed to parse date(-time) from '$dt'");
        ConstNull
      }
    } else if (isOffsetDateTime(dt)) {
      Try(ConstDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse date-time from '$dt'");
        ConstNull
      }
    } else if (isLocalDateTime(dt)) {
      Try(ConstLocalDateTime(dt)).getOrElse {
        logger.warn(s"Failed to parse local-date-time from '$dt'");
        ConstNull
      }
    } else {
      logger.warn(s"Failed to parse date-time from '$dt'");
      ConstNull
    }
  }

  private def parseDuration(d: String): Exp = {
    if (isYearMonthDuration(d)) {
      Try(ConstYearMonthDuration(d)).getOrElse {
        logger.warn(s"Failed to parse year-month-duration from '$d'");
        ConstNull
      }
    } else if (isDayTimeDuration(d)) {
      Try(ConstDayTimeDuration(d)).getOrElse {
        logger.warn(s"Failed to parse day-time-duration from '$d'");
        ConstNull
      }
    } else {
      logger.warn(s"Failed to parse duration from '$d'");
      ConstNull
    }
  }
}

/** `JavaTokenParsers` differs from [[scala.util.parsing.combinator.RegexParsers]]
  * by adding the following definitions:
  *
  *  - `ident`
  *  - `wholeNumber`
  *  - `decimalNumber`
  *  - `stringLiteral`
  *  - `floatingPointNumber`
  */
trait JavaTokenParsers {

  /** Anything that is a valid Java identifier, according to
    * <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">The Java Language Spec</a>.
    * Generally, this means a letter, followed by zero or more letters or numbers.
    */
  def ident[_: P]: P[String] =
    P[String](
      (
        CharsWhile(Character.isJavaIdentifierStart, 1) ~~
          CharsWhile(Character.isJavaIdentifierPart, 0)
      ).!
    )

  def digits[_: P]: P[String] = P[String](CharIn("0-9").rep(1).!)

  /** An integer, without sign or with a negative sign. */
  def wholeNumber[_: P]: P[String] =
    P[String](("-".!.? ~ digits).map {
      case (minus, digits) =>
        s"${minus.getOrElse("")}$digits"
    })

}
