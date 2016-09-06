package org.camunda.feel.parser

import org.camunda.feel._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Philipp Ossler
 *
 * @ss DMN 1.0 (S.99)
 */
object FeelParser extends JavaTokenParsers {

  // override to ignore comment '// ...' and '/* ... */'
  protected override val whiteSpace = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  def parseSimpleExpression(exp: String): ParseResult[Exp] = parseAll(simpleExpression, exp)
  
  def parseExpression(exp: String): ParseResult[Exp] = parseAll(expression, exp)
  
  def parseSimpleUnaryTest(expression: String): ParseResult[Exp] = parseAll(simpleUnaryTests, expression)

  private val reservedWord = "not" | "-" | "+" | "*" | "/" | "**" | "date" | "time" | "duration"

  // 1
  private def expression: Parser[Exp] = textualExpression
  
  // 2
  private def textualExpression = ( comparison 
    | arithmeticExpression 
    | functionInvocation
    | literal 
    | name )
  
  // 4
  private def arithmeticExpression = ( addition | subtraction | multiplication | division
    | exponentiation | arithmeticNegation
    | failure("ilegal start of an arithmetic expression. expect an operator of '+', '-', '*', '/', '**' or a negation '-'."))

  // 5
  private def simpleExpression = arithmeticExpression | comparison | simpleValue

  // 6
  private def simpleExpressions = (simpleExpression ~ "," ~ repsep(simpleExpression, ",") ^^ { case x ~ _ ~ xs => 
     failure("simple expressions are  not supported since I dont understand how to interpret it") }
      | simpleExpression)

  // 7 - compare for number, dates, time, duration
  private def simplePositivUnaryTest = ("<" ~> compareableLiteral ^^ { case x => InputLessThan(x) }
    | "<=" ~> compareableLiteral ^^ { case x => InputLessOrEqual(x) }
    | ">" ~> compareableLiteral ^^ { case x => InputGreaterThan(x) }
    | ">=" ~> compareableLiteral ^^ { case x => InputGreaterOrEqual(x) }
    | interval
    | endpoint ^^ { case x => InputEqualTo(x) }
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

  // 21 - TODO: both should be expressions
  private def addition = simpleValue ~ "+" ~ expression ^^ { case x ~ _ ~ y => Addition(x, y) }

  // 22 - TODO: both should be expressions
  private def subtraction = simpleValue ~ "-" ~ expression ^^ { case x ~ _ ~ y => Subtraction(x, y) }

  // 23 - TODO: both should be expressions
  private def multiplication = simpleValue ~ "*" ~ expression ^^ { case x ~ _ ~ y => Multiplication(x, y) }

  // 24 - TODO: both should be expressions
  private def division = simpleValue ~ "/" ~ expression ^^ { case x ~ _ ~ y => Division(x, y) }

  // 25 - TODO: both should be expressions
  private def exponentiation = simpleValue ~ "**" ~ expression ^^ { case x ~ _ ~ y => Exponentiation(x, y) }

  // 26
  private def arithmeticNegation = "-" ~> expression ^^ { case x => ArithmeticNegation(x) }

  // 27
  private def name = identifier ^^ (s => Ref(s))

  private def identifier = not(reservedWord) ~> ident

  // 33
  private def literal: Parser[Exp] = ( simpleLiteral
      | "null" ^^ (_ => ConstNull) )
  
  // 34
  private def simpleLiteral = numericLiteral | booleanLiteral | dateTimeLiternal | stringLiteraL
  
  // 34
  // naming clash with JavaTokenParser
  private def stringLiteraL: Parser[Exp] = "\"" ~> ("""[a-zA-Z_]\w*""".r) <~ "\"" ^^ { case s => ConstString(s) }

  // 35
  private def booleanLiteral: Parser[Exp] = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))

  // 36
  private def numericLiteral = """(\d+(\.\d+)?|\d*\.\d+)""".r ^^ (n => ConstNumber(n))
  
  // 39
  private def dateTimeLiternal: Parser[Exp] = ("date(" ~> stringLiteral <~ ")" ^^ { case date => ConstDate(withoutQuotes(date)) }
    | "time(" ~> stringLiteral <~ ")" ^^ { case time => ConstTime(withoutQuotes(time)) }
    | "duration(" ~> stringLiteral <~ ")" ^^ { case duration => ConstDuration(withoutQuotes(duration)) }
    | failure("illegal start of a date time literal. expect a date ('YYYY-MM-DD'), time ('hh:mm:ss') or duration ('PnYnMnDTnHnMnS')"))

  private def withoutQuotes(exp: String): String = exp.replaceAll("\"", "")

  // 40 - name should be an expression
  private def functionInvocation = identifier ~ parameters ^^ { case name ~ params => FunctionInvocation(name, params) }
  
  // 41 - TODO: add named params
  private def parameters = "(" ~> positionalParameters <~ ")"

  // 44
  private def positionalParameters = repsep(expression, ",")
  
  // 51 - TODO: both should be expressions
  private def comparison = simpleValue ~ ("<=" | ">=" | "<" | ">" | "!=" | "=") ~ expression ^^ {
    case x ~ "=" ~ y => Equal(x, y)
    case x ~ "!=" ~ y => Not(Equal(x, y))
    case x ~ "<" ~ y => LessThan(x, y)
    case x ~ "<=" ~ y => LessOrEqual(x, y)
    case x ~ ">" ~ y => GreaterThan(x, y)
    case x ~ ">=" ~ y => GreaterOrEqual(x, y)
  }

}