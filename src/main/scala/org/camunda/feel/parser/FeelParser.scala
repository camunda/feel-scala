package org.camunda.feel.parser

import scala.util.parsing.combinator.JavaTokenParsers
import org.camunda.feel.types.Number
import org.joda.time.LocalDate
import org.camunda.feel.types.OpenIntervalBoundary
import org.camunda.feel.types.OpenIntervalBoundary

/**
 * @author Philipp Ossler
 * 
 * @ss DMN 1.0 (S.99)
 */
class FeelParser extends JavaTokenParsers {
  
  def parse(expression: String): ParseResult[Exp] = parseAll(program, expression)
  
  def parseTest(expression: String): ParseResult[Exp] = parseAll(simpleUnaryTests, expression)
  
  
  private def program: Parser[Exp] = expression
  
  // 1
  private def expression = textualExpression // ...
  
  // 2
  private def textualExpression = simplePositivUnaryTest | literal // ...
  
  // 5
  private def simpleExpression = simpleValue // arithmeticExpressio
  
  // 7
  // compare for number, dates, time, duration
  private def simplePositivUnaryTest = (  "<"  ~ compareableLiteral ^^ { case op ~ x => LessThan(x) }
                                        | "<=" ~ compareableLiteral ^^ { case op ~ x => LessOrEqual(x)}
                                        | ">"  ~ compareableLiteral ^^ { case op ~ x => GreaterThan(x)}
                                        | ">=" ~ compareableLiteral ^^ { case op ~ x => GreaterOrEqual(x)} 
                                        | interval
                                        )
  
  // all types that can compare with unary operator
  private def compareableLiteral = numericLiteral | dateTimeLiternal
  
  private def interval = ( "(" | "]" ) ~ compareableLiteral ~ ".." ~ compareableLiteral ~ ( ")" | "[" ) ^^ { case _ ~ start ~ _ ~ end ~ _ => Interval(OpenIntervalBoundary(start), OpenIntervalBoundary(end)) }
                          
  
  // 14
  private def simpleUnaryTests = simplePositivUnaryTest // ...
  
  // 18
  private def endpoint = simpleValue 
  
  // 19
  private def simpleValue = simpleLiteral // ...
  
  // 33
  private def literal = simpleLiteral // ...
  
  // 33
  private def simpleLiteral = numericLiteral | booleanLiteral | dateTimeLiternal | stringLiteraL  // | date time literal
  
  // 36
  private def numericLiteral = decimalNumber ^^ ( n => ConstNumber(n.toDouble) )
  
  // 34
  // naming clash with JavaTokenParser
  private def stringLiteraL = ("""[a-zA-Z_]\w*""".r) ^^ ( s => ConstString(s) )
  
  // 35
  private def booleanLiteral: Parser[Exp] = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))
  
  // 39
  private def dateTimeLiternal: Parser[Exp] = "date" ~ "(" ~ stringLiteral ~ ")" ^^ { case op ~ _ ~ date ~ _ => ConstDate(LocalDate.parse(date.replaceAll("\"", ""))) }
}