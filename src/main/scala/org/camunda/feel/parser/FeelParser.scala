package org.camunda.feel.parser

import org.camunda.feel._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Philipp Ossler
 *
 * @ss DMN 1.0 (S.99)
 */
class FeelParser extends JavaTokenParsers {

  def parseSimpleExpression(expression: String): ParseResult[Exp] = parseAll(simpleExpression, expression)

  def parseSimpleUnaryTest(expression: String): ParseResult[Exp] = parseAll(simpleUnaryTests, expression)

  private def program: Parser[Exp] = expression

  // 1
  private def expression = textualExpression // ...

  // 2
  private def textualExpression = simplePositivUnaryTest | literal // ...

  // 5
  private def simpleExpression = simpleValue // arithmeticExpressio

  
  // 7 - compare for number, dates, time, duration
  private def simplePositivUnaryTest = ("<" ~ compareableLiteral ^^ { case op ~ x => LessThan(x) }
    | "<=" ~ compareableLiteral ^^ { case op ~ x => LessOrEqual(x) }
    | ">" ~ compareableLiteral ^^ { case op ~ x => GreaterThan(x) }
    | ">=" ~ compareableLiteral ^^ { case op ~ x => GreaterOrEqual(x) }
    | interval
    | simpleValue ^^ { case x => Equal(x) }  
  )

  // all types that can compare with operator '<', '<=', '>' and '>='
  private def compareableLiteral = numericLiteral | dateTimeLiternal

  // 8
  private def interval = ("(" | "]" | "[") ~ compareableLiteral ~ ".." ~ compareableLiteral ~ (")" | "[" | "]") ^^ {
    case ("(" | "]") ~ start ~ _ ~ end ~ (")" | "[") => Interval(OpenIntervalBoundary(start), OpenIntervalBoundary(end))
    case ("(" | "]") ~ start ~ _ ~ end ~ "]" => Interval(OpenIntervalBoundary(start), ClosedIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ (")" | "[") => Interval(ClosedIntervalBoundary(start), OpenIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ "]" => Interval(ClosedIntervalBoundary(start), ClosedIntervalBoundary(end))
  }
  
   // 13
  private def simplePositivUnaryTests = ( simplePositivUnaryTest ~ "," ~ repsep(simplePositivUnaryTest, ",") ^^ { case x ~ _ ~ xs => AtLeastOne(x :: xs) }
                                        | simplePositivUnaryTest )
 
  // 14
  private def simpleUnaryTests = simplePositivUnaryTests // ...

  // 18
  private def endpoint = simpleValue

  // 19
  private def simpleValue = simpleLiteral // ...

  // 33
  private def literal = simpleLiteral // ...

  // 33
  private def simpleLiteral = numericLiteral | booleanLiteral | dateTimeLiternal | stringLiteraL // ...

  // 36
  private def numericLiteral = decimalNumber ^^ (n => ConstNumber(n))

  // 34
  // naming clash with JavaTokenParser
  private def stringLiteraL: Parser[Exp] = "\"" ~ ("""[a-zA-Z_]\w*""".r) ~ "\"" ^^ { case _ ~ s ~ _ => ConstString(s) }

  // 35
  private def booleanLiteral: Parser[Exp] = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))

  // 39
  private def dateTimeLiternal: Parser[Exp] = "date" ~ "(" ~ stringLiteral ~ ")" ^^ { case op ~ _ ~ date ~ _ => ConstDate(date.replaceAll("\"", "")) }
  
}