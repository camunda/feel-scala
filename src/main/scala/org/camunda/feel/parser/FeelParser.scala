package org.camunda.feel.parser

import org.camunda.feel._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Philipp Ossler
 *
 * @ss DMN 1.0 (S.99)
 */
object FeelParser extends JavaTokenParsers {

  def parseSimpleExpression(expression: String): ParseResult[Exp] = parseAll(simpleExpression, expression)

  def parseSimpleUnaryTest(expression: String): ParseResult[Exp] = parseAll(simpleUnaryTests, expression)

  private val reservedWord = "not" | "-" | "date" | "time" | "duration"

  // 5
  private def simpleExpression = simpleValue // arithmeticExpression

  // 7 - compare for number, dates, time, duration
  private def simplePositivUnaryTest = ("<" ~> compareableLiteral ^^ { case x => LessThan(x) }
    | "<=" ~> compareableLiteral ^^ { case x => LessOrEqual(x) }
    | ">" ~> compareableLiteral ^^ { case x => GreaterThan(x) }
    | ">=" ~> compareableLiteral ^^ { case x => GreaterOrEqual(x) }
    | interval
    | endpoint ^^ { case x => Equal(x) }
    | failure("illegal start of simple positiv unary test. expect a compare operator ('<', '<=', '>', '>='), an interval ('[..]', '(..)', ']..['), a simple literal or a qualified name."))

  // all types that can compare with operator '<', '<=', '>' and '>='
  private def compareableLiteral = (numericLiteral
    | dateTimeLiternal
    | qualifiedName
    | failure("illegal argument for compare operator. expect a number, a date or a qualified name."))

  // 8
  private def interval = ("(" | "]" | "[") ~ compareableLiteral ~ ".." ~ compareableLiteral ~ (")" | "[" | "]") ^^ {
    case ("(" | "]") ~ start ~ _ ~ end ~ (")" | "[") => Interval(OpenIntervalBoundary(start), OpenIntervalBoundary(end))
    case ("(" | "]") ~ start ~ _ ~ end ~ "]" => Interval(OpenIntervalBoundary(start), ClosedIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ (")" | "[") => Interval(ClosedIntervalBoundary(start), OpenIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ "]" => Interval(ClosedIntervalBoundary(start), ClosedIntervalBoundary(end))
  }

  // 13
  private def simplePositivUnaryTests = (simplePositivUnaryTest ~ "," ~ repsep(simplePositivUnaryTest, ",") ^^ { case x ~ _ ~ xs => AtLeastOne(x :: xs) }
    | simplePositivUnaryTest)

  // 14
  private def simpleUnaryTests = (
    "-" ^^ (_ => ConstBool(true))
    | "not(" ~> simplePositivUnaryTests <~ ")" ^^ { case x => Not(x) }
    | simplePositivUnaryTests
    | failure("illegal start of simple unary test. expect simple positiv unary tests (e.g. compare operator, interval, literal, qualified name), a 'not' operator or an empty test (eg. '-')"))

  // 18
  private def endpoint = simpleValue

  // 19
  private def simpleValue = simpleLiteral | qualifiedName

  // 20 
  private def qualifiedName = (rep1sep(identifier, ".") ^^ { case xs => Ref(xs mkString ".") }
    | name)

  // 27
  private def name = identifier ^^ (s => Ref(s))

  private def identifier = not(reservedWord) ~> ident

  // 33
  private def simpleLiteral = numericLiteral | booleanLiteral | dateTimeLiternal | stringLiteraL

  // 36
  private def numericLiteral = """(\d+(\.\d+)?|\d*\.\d+)""".r ^^ (n => ConstNumber(n))

  // 34
  // naming clash with JavaTokenParser
  private def stringLiteraL: Parser[Exp] = "\"" ~> ("""[a-zA-Z_]\w*""".r) <~ "\"" ^^ { case s => ConstString(s) }

  // 35
  private def booleanLiteral: Parser[Exp] = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))

  // 39
  private def dateTimeLiternal: Parser[Exp] = ("date(" ~> stringLiteral <~ ")" ^^ { case date => ConstDate(withoutQuotes(date)) }
    | "time(" ~> stringLiteral <~ ")" ^^ { case time => ConstTime(withoutQuotes(time)) }
    | "duration(" ~> stringLiteral <~ ")" ^^ { case duration => ConstDuration(withoutQuotes(duration)) })

  private def withoutQuotes(exp: String): String = exp.replaceAll("\"", "")

}