package org.camunda.feel.parser

import scala.util.parsing.combinator.JavaTokenParsers
import org.camunda.feel.types.Number

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
  private def textualExpression = simplePositivUnaryTest // ...
  
  // 5
  private def simpleExpression = simpleValue // arithmeticExpressio
  
  // 7
  // compare for number, dates, time, duration
  private def simplePositivUnaryTest = (  "<"  ~ numericLiteral ^^ { case op ~ x => LessThan(x) }
                                        | "<=" ~ numericLiteral ^^ { case op ~ x => LessOrEqual(x)}
                                        | ">"  ~ numericLiteral ^^ { case op ~ x => GreaterThat(x)}
                                        | ">=" ~ numericLiteral ^^ { case op ~ x => GreaterOrEqual(x)} 
                                        )
                              
  
  // 14
  private def simpleUnaryTests = simplePositivUnaryTest // ...
  
  // 18
  private def endpoint = simpleValue 
  
  // 19
  private def simpleValue = simpleLiteral // ...
  
  // 33
  private def simpleLiteral = numericLiteral | booleanLiteral | stringLiteraL  // | date time literal
  
  // 36
  private def numericLiteral = wholeNumber ^^ ( n => ConstNumber(n.toDouble) ) 
  
  // 34
  // naming clash with JavaTokenParser
  private def stringLiteraL = ("""[a-zA-Z_]\w*""".r) ^^ ( s => ConstString(s) )
  
  // 35
  private def booleanLiteral = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))
  
  
}