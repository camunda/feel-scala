package org.camunda.feel.parser

import org.camunda.feel._

import scala.util.parsing.combinator.JavaTokenParsers
import com.sun.org.apache.bcel.internal.generic.IFEQ

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

  private val reservedWord = ( "null"
    | "not" 
    | "-" | "+" | "*" | "/" | "**" 
    | "date" | "time" | "duration"
    | "function"
    | "if" | "then" | "else"
    | "or" | "and" | "between"
    | "instance" | "of" )
      
  // safe recursive expressions  
  private def atom =  ( boxedExpression | functionDefinition | functionInvocation
    | forExpression | ifExpression | quantifiedExpression
    | literal | name | simplePositivUnaryTest 
    | "(" ~> textualExpression <~ ")" )
    
  // 1
  private def expression: Parser[Exp] = textualExpression | boxedExpression
  
  // 2
  private def textualExpression: Parser[Exp] = ( functionDefinition
    | forExpression  
    | ifExpression
    | quantifiedExpression
    | instanceOf
    | comparison 
    | disjunction
    | conjunction
    | arithmeticExpression 
    | pathExpression
    | filter
    | functionInvocation
    | literal 
    | name
    | simplePositivUnaryTest
    | "(" ~> textualExpression <~ ")" )
    
  // 4
  private def arithmeticExpression = ( addition | subtraction | multiplication | division
    | exponentiation | arithmeticNegation
    | failure("ilegal start of an arithmetic expression. expect an operator of '+', '-', '*', '/', '**' or a negation '-'."))

  // 5
  private def simpleExpression = arithmeticExpression | simpleValue

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
    | "not(" ~> simplePositivUnaryTests <~ ")" ^^ { Not }
    | simplePositivUnaryTests
    | failure("illegal start of simple unary test. expect simple positiv unary tests (e.g. compare operator, interval, literal, qualified name), a 'not' operator or an empty test (eg. '-')"))

  // 15
  private def positiveUnaryTest = ( simplePositivUnaryTest 
    | "null" ^^ ( _ => InputEqualTo(ConstNull)) ) 
    
  // 16
  private def positiveUnaryTests = (positiveUnaryTest ~ "," ~ repsep(positiveUnaryTest, ",") ^^ { case x ~ _ ~ xs => AtLeastOne(x :: xs) }
    | positiveUnaryTest)   
    
  // 17
  private def unaryTest = ( 
    "-" ^^ ( _ => ConstBool(true) )
    | "not(" ~> positiveUnaryTests <~ ")" ^^ ( Not )
    | positiveUnaryTests 
    | failure("illegal start of unary test. expect positive unary tests (i.e. simple unary test or 'null'), a 'not' operator or an empty test (eg. '-')") )
    
  // 18
  private def endpoint = simpleValue

  // 19
  private def simpleValue = simpleLiteral | qualifiedName

  // 20 
  private def qualifiedName = (rep1sep(identifier, ".") ^^ { case xs => Ref(xs mkString ".") }
    | name)

  // 21
  private def addition = atom ~ "+" ~ expression ^^ { case x ~ _ ~ y => Addition(x, y) }

  // 22
  private def subtraction = atom ~ "-" ~ expression ^^ { case x ~ _ ~ y => Subtraction(x, y) }

  // 23
  private def multiplication = atom ~ "*" ~ expression ^^ { case x ~ _ ~ y => Multiplication(x, y) }

  // 24
  private def division = atom ~ "/" ~ expression ^^ { case x ~ _ ~ y => Division(x, y) }

  // 25
  private def exponentiation = atom ~ "**" ~ expression ^^ { case x ~ _ ~ y => Exponentiation(x, y) }

  // 26
  private def arithmeticNegation = "-" ~> expression ^^ { case x => ArithmeticNegation(x) }

  // 27 - TODO use FEEL name definition
  private def name = identifier ^^ (s => Ref(s))

  private def identifier = not(reservedWord) ~> ident
  
  // 27 - without additional name symbols
  private def feelName: Parser[String] = nameStart ~ repsep( namePart, "") ^^ { case s ~ ps => s + ps.mkString }
  
  // 28
  private def nameStart: Parser[String] = nameStartChar ~ repsep(namePartChar, "") ^^ { case s ~ ps => s + ps.mkString }
  
  // 29
  private def namePart: Parser[String] = rep1sep(namePartChar, "") ^^ (_.mkString)
  
  // 30 - without whitespaces
  // - no unicode chars? "[\\uC0-\\uD6]".r | "[\\uD8-\\uF6]".r | "[\\uF8-\\u2FF]".r | "[\\u370-\\u37D]".r | "[\\u37F-\u1FFF]".r 
  private def nameStartChar: Parser[String] = ( "?" | "[A-Z]".r |"[a-z]".r 
    | "[\u200C-\u200D]".r | "[\u2070-\u218F]".r | "[\u2C00-\u2FEF]".r | "[\u3001-\uD7FF]".r | "[\uF900-\uFDCF]".r | "[\uFDF0-\uFFFD]".r | "[\u10000-\uEFFFF]".r        
  ) ^^ (s => s)
  
  // 31
  private def namePartChar: Parser[String] = (nameStartChar | "[\u0300-\u036F]".r | "[\u203F-\u2040]".r)| digit ^^ (d => d.toString)
  
  // 32
  private def additionalNameSymbols: Parser[String] = "." | "/" | "-" | "’" | "+" | "*" 
  
  // 33
  private def literal: Parser[Exp] = ( simpleLiteral
      | "null" ^^ (_ => ConstNull) )
  
  // 34
  private def simpleLiteral = numericLiteral | booleanLiteral | dateTimeLiternal | stringLiteraL
  
  // 35
  // naming clash with JavaTokenParser
  private def stringLiteraL: Parser[Exp] = "\"" ~> ("""[a-zA-Z_]\w*""".r) <~ "\"" ^^ { case s => ConstString(s) }

  // 36
  private def booleanLiteral: Parser[Exp] = ("true" | "false") ^^ (b => ConstBool(b.toBoolean))

  // 37
  private def numericLiteral: Parser[ConstNumber] = """(-?(\d+(\.\d+)?|\d*\.\d+))""".r ^^ (x => ConstNumber(x))
    
  // 38
  private def digit: Parser[Number] = "[0-9]".r ^^ ( d => d)
  
  // 39
  private def digits: Parser[Number] = rep1sep(digit, "") ^^ ( _.mkString )
  
  // 39
  private def dateTimeLiternal: Parser[Exp] = (
      "date(" ~> stringLiteral <~ ")" ^^ { case date => ConstDate(withoutQuotes(date)) }
    | "time(" ~> stringLiteral <~ ")" ^^ { case time => ConstTime(withoutQuotes(time)) }
    | "duration(" ~> stringLiteral <~ ")" ^^ { case duration => ConstDuration(withoutQuotes(duration)) }
    | failure("illegal start of a date time literal. expect a date ('YYYY-MM-DD'), time ('hh:mm:ss') or duration ('PnYnMnDTnHnMnS')"))

  private def withoutQuotes(exp: String): String = exp.replaceAll("\"", "")

  // 40 - name should be an expression
  private def functionInvocation = identifier ~ parameters ^^ { case name ~ params => FunctionInvocation(name, params) }
  
  // 41
  private def parameters = "(" ~> ( namedParameters | positionalParameters ) <~ ")"

  // 42
  private def namedParameters = rep1sep(namedParameter , ",") ^^ (params => NamedFunctionParameters(params.toMap) )
  
  private def namedParameter = parameterName ~ ":" ~ expression ^^ { case name ~ _ ~ value => (name, value) }
  
  // 43
  private def parameterName = identifier
  
  // 44
  private def positionalParameters = repsep(expression, ",") ^^ (params => PositionalFunctionParameters(params) )
  
  // 45 - enables recursive path expressions
  private def pathExpression = atom ~ "." ~ rep1sep(identifier, ".") ^^ { 
    case exp ~ _ ~ (key :: keys) => keys.foldLeft(PathExpression(exp, key)) { (path,k) => PathExpression(path, k) }}
  
  // 46
  private def forExpression = "for" ~> rep1sep(listIterator, ",") ~ "return" ~ expression ^^ {
    case iterators ~ _ ~ exp => For(iterators, exp)
  }
  
  private def listIterator = identifier ~ "in" ~ expression ^^ { case name ~ _ ~ list => (name, list) }
  
  // 47 
  private def ifExpression = "if" ~> expression ~ "then" ~ expression ~ "else" ~ expression ^^ {
    case condition ~ _ ~ then ~ _ ~ otherwise => If(condition, then, otherwise)
  }
  
  // 48 - TODO: should be multiple lists - but how to separate them?
  private def quantifiedExpression = ("some" | "every") ~ listIterator ~ "satisfies" ~ expression ^^ {
  	case "some" ~ Tuple2(name, list) ~ _ ~ condition => SomeItem(name, list, condition)
  	case "every" ~ Tuple2(name, list) ~ _ ~ condition => EveryItem(name, list, condition)
  }
  
  // 49
  private def disjunction = atom ~ "or" ~ expression ^^ { case x ~ _ ~ y => Disjunction(x, y) }
  
  // 50
  private def conjunction = atom ~ "and" ~ expression ^^ { case x ~ _ ~ y => Conjunction(x,y) }
  
  // 51
  private def comparison = ( simpleComparison
      | atom ~ "between" ~ atom ~ "and" ~ expression ^^ { case x ~ _ ~ a ~ _ ~ b => Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b)) }
      | atom ~ "in" ~ positiveUnaryTest ^^ { case x ~ _ ~ test => In(x, test) }
      | atom ~ "in" ~ "(" ~ positiveUnaryTests <~ ")" ^^ { case x ~ _ ~ _ ~ tests => In(x, tests) }
  )
  
  private def simpleComparison = atom ~ ("<=" | ">=" | "<" | ">" | "!=" | "=") ~ expression ^^ {
    case x ~ "=" ~ y => Equal(x, y)
    case x ~ "!=" ~ y => Not(Equal(x, y))
    case x ~ "<" ~ y => LessThan(x, y)
    case x ~ "<=" ~ y => LessOrEqual(x, y)
    case x ~ ">" ~ y => GreaterThan(x, y)
    case x ~ ">=" ~ y => GreaterOrEqual(x, y)
  }
  
  // 52
  private def filter = (atom | list) ~ "[" ~ expression ~ "]" ^^ { case list ~ _ ~ filter ~ _ => Filter(list, filter) } 
  
  // 53
  private def instanceOf = atom ~ "instance" ~ "of" ~ typeName ^^ { case x ~ _ ~ _ ~ typeName => InstanceOf(x, typeName) }
  
  // 54 - TODO: should be qualified name
  private def typeName = identifier
  
  // 55
  private def boxedExpression = list | functionDefinition | context
  
  // 56
  private def list = "[" ~> repsep(expression, ",") <~ "]" ^^ ( entries => ConstList(entries) )
  
  // 57
  private def functionDefinition = "function" ~ "(" ~ repsep(formalParameter, ",") ~ ")" ~ expression ^^ { case _ ~ _ ~ params ~ _ ~ body => FunctionDefinition(params, body) }
  
  // 58
  private def formalParameter = parameterName
  
  // 59
  private def context = "{" ~> repsep(contextEntry, ",") <~ "}" ^^ { case entries => ConstContext(entries) }
  
  // 60 
  private def contextEntry = key ~ ":" ~ expression ^^ { case key ~ _ ~ value => (key -> value) }
  
  // 61 - TODO: should be also string literal
  private def key = identifier
  
}