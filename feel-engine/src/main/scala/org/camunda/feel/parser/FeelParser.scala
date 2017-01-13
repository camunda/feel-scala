package org.camunda.feel.parser

import org.camunda.feel._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Philipp Ossler
 *
 * @ss DMN 1.0 (S.99)
 */
object FeelParser extends JavaTokenParsers {

  def parseSimpleExpression(exp: String): ParseResult[Exp] = parseExp(simpleExpression, exp)
  
  def parseExpression(exp: String): ParseResult[Exp] = parseExp(expression, exp)
  
  def parseSimpleUnaryTests(expression: String): ParseResult[Exp] = parseExp(simpleUnaryTests, expression)
  
  private def parseExp[T](parser: Parser[T], exp: String) = {
  	val start = System.currentTimeMillis
  	
  	val result = parseAll(parser, exp)
  	
  	val duration = System.currentTimeMillis - start  	
  	if (duration > 1000) {
  		System.err.println(s"parsing of expression takes $duration ms: $exp")
  	}
  	
  	result
  }
  
  // override to ignore comment '// ...' and '/* ... */'
  protected override val whiteSpace = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  private lazy val reservedWord = "null" | "true" | "false" | "function" | "if" | "then" | "else" | "for" | "between" | "instance" | "of"
      
  private lazy val identifier = not(reservedWord) ~> ident 
  
  private lazy val stringLiteralWithQuotes: Parser[String] = stringLiteral ^^ ( _.replaceAll("\"", "") ) 
    
  private lazy val terminal =  literal | name ^^ ( n => Ref(List(n)) ) | list | context
    
  // 1 a)
  private lazy val expression: Parser[Exp] = textualExpression
  // 1 b)
  private lazy val expression10 = boxedExpression
  
  // 3
  private lazy val textualExpressions: Parser[ConstList] = rep1sep(textualExpression, ",") ^^ ConstList
  
  // 2 a)
  private lazy val textualExpression: Parser[Exp] = functionDefinition | forExpression | ifExpression | quantifiedExpression | expression2
  // 2b)
  private lazy val expression2 = disjunction
  // 2 c)
  private lazy val expression3 = conjunction
  // 2 d)
  private lazy val expression4 = comparison | expression5
  // 2 e)
  private lazy val expression5 = arithmeticExpression
  // 2 f)
  private lazy val expression6 = instanceOf | expression7
  // 2 g)
  private lazy val expression7 = pathExpression
  // 2 h)
  private lazy val expression8 = filterExpression | functionInvocation | expression9
  // 2 i)
  private lazy val expression9 = literal | name ^^ ( n => Ref(List(n)) ) | simplePositiveUnaryTest | "(" ~> textualExpression <~ ")" | expression10
    
  // 6
  private lazy val simpleExpressions: Parser[ConstList] = rep1sep(simpleExpression, ",") ^^ ConstList
  
  // 5
  private lazy val simpleExpression: Parser[Exp] = arithmeticExpression | simpleValue
  
  // 4 a) -> 21+22
  private lazy val arithmeticExpression = chainl1(arithmeticExpression2, "+" ^^^ Addition | "-" ^^^ Subtraction )
  // 4 b) -> 23+24
  private lazy val arithmeticExpression2 = chainl1(arithmeticExpression3, "*" ^^^ Multiplication | "/" ^^^ Division )
  // 4 c) -> 25
  private lazy val arithmeticExpression3 = chainl1(arithmeticExpression4, "**" ^^^ Exponentiation )
  // 4 d) -> 26
  private lazy val arithmeticExpression4 = opt("-") ~! expression6 ^^ {  case Some(_) ~ e => ArithmeticNegation(e)
                                                                        case None ~ e => e }
  
  // 17
  private lazy val unaryTest: Parser[Exp] = 
    "-" ^^^ ConstBool(true) |
    "not" ~! "(" ~> positiveUnaryTests <~ ")" ^^ Not |
    positiveUnaryTests
    
  // 16
  private lazy val positiveUnaryTests: Parser[Exp] = rep1sep(positiveUnaryTest, ",") ^^ {  case test :: Nil => test
  	                                                                                       case tests => AtLeastOne(tests) }
  
  // 15
  private lazy val positiveUnaryTest: Parser[Exp] = "null" ^^^ InputEqualTo(ConstNull) | simplePositiveUnaryTest    
    
  // 14
  private lazy val simpleUnaryTests: Parser[Exp] =
    "-" ^^^ ConstBool(true) |
    "not" ~! "(" ~> simplePositiveUnaryTests <~ ")" ^^ Not |
    simplePositiveUnaryTests
    
  // 13
  private lazy val simplePositiveUnaryTests: Parser[Exp] = rep1sep(simplePositiveUnaryTest, ",") ^^ {  case test :: Nil => test
                                                                                                       case tests => AtLeastOne(tests) }
  
  // 7
  private lazy val simplePositiveUnaryTest: Parser[Exp] = 
    "<" ~> endpoint ^^ InputLessThan|
    "<=" ~> endpoint ^^ InputLessOrEqual |
    ">" ~> endpoint ^^ InputGreaterThan |
    ">=" ~> endpoint ^^ InputGreaterOrEqual |
    endpoint ^^ InputEqualTo |
    interval
    
  // 18
  private lazy val endpoint: Parser[Exp] = simpleValue
    
  // 19
  private lazy val simpleValue = simpleLiteral | qualifiedName ^^ ( Ref(_) )
  
  // 33
  private lazy val literal: Parser[Exp] = "null" ^^^ ConstNull | simpleLiteral
  
  // 34
  private lazy val simpleLiteral = booleanLiteral | dateTimeLiternal | stringLiteraL | numericLiteral
  
  // 36
  private lazy val booleanLiteral: Parser[ConstBool] = "true" ^^^ ConstBool(true) | "false" ^^^ ConstBool(false)

  // 62
  private lazy val dateTimeLiternal: Parser[Exp] =
    "date" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ ( ConstDate(_) ) |
    "time" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ ( ConstTime(_) ) |
    "date and time" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ ( ConstDateTime(_) ) |
    "duration" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ ( d => if(isYearMonthDuration(d)) ConstYearMonthDuration(d) else ConstDayTimeDuration(d)) |
    failure("expected date time literal")
    
  // 35 - 
  private lazy val stringLiteraL: Parser[ConstString] = stringLiteralWithQuotes ^^ ConstString

  // 37 - use combined regex instead of multiple parsers
  private lazy val numericLiteral: Parser[ConstNumber] = """(-?(\d+(\.\d+)?|\d*\.\d+))""".r ^^ ( ConstNumber(_) )
    
  // 39
  private lazy val digits: Parser[String] = rep1(digit) ^^ ( _.mkString )

  // 38
  private lazy val digit: Parser[String] = "[0-9]".r
  
  // 20 
  private lazy val qualifiedName: Parser[List[String]] = rep1sep(name, ".")
  
  // 27 - simplified name definition
  private lazy val name: Parser[String] = identifier
  
  // FEEL name definition
  private lazy val feelName: Parser[String] = nameStart ~! rep( namePart | additionalNameSymbols ) ^^ { case s ~ ps => s + ps.mkString }
  
  // 28
  private lazy val nameStart = nameStartChar ~! rep(namePartChar) ^^ { case s ~ ps => s + ps.mkString }
  
  // 29
  private lazy val namePart = rep1(namePartChar) ^^ ( _.mkString )
  
  // 30- unknown unicode chars "[\\uC0-\\uD6]".r | "[\\uD8-\\uF6]".r | "[\\uF8-\\u2FF]".r | "[\\u370-\\u37D]".r | "[\\u37F-\u1FFF]".r 
  private lazy val nameStartChar = "?" | "[A-Z]".r | "_" |"[a-z]".r |
    "[\u200C-\u200D]".r | "[\u2070-\u218F]".r | "[\u2C00-\u2FEF]".r | "[\u3001-\uD7FF]".r | "[\uF900-\uFDCF]".r | "[\uFDF0-\uFFFD]".r | "[\u10000-\uEFFFF]".r        
  
  // 31 - unknown unicode char "[\\uB7]".r
  private lazy val namePartChar = nameStartChar | digit | "[\u0300-\u036F]".r | "[\u203F-\u2040]".r
  
  // 32 
  private lazy val additionalNameSymbols = "." | "/" | "-" | "’" | "+" | "*" 
  
  // 8
  private lazy val interval: Parser[Interval] = (openIntervalStart | closedIntervalStart) ~ endpoint ~ ".." ~! endpoint ~! (openIntervalEnd | closedIntervalEnd) ^^ {
    case ( "(" | "]" ) ~ start ~ _ ~ end ~ ( ")" | "[" ) => Interval(OpenIntervalBoundary(start), OpenIntervalBoundary(end))
    case ( "(" | "]" ) ~ start ~ _ ~ end ~ "]" => Interval(OpenIntervalBoundary(start), ClosedIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ ( ")" | "[" ) => Interval(ClosedIntervalBoundary(start), OpenIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ "]" => Interval(ClosedIntervalBoundary(start), ClosedIntervalBoundary(end))
  }

  // 9
  private lazy val openIntervalStart = "(" | "]"

  // 10 
	private lazy val closedIntervalStart = "["

	// 11
	private lazy val openIntervalEnd = ")" | "["

	// 12
	private lazy val closedIntervalEnd = "]"
  
  // 46
  private lazy val forExpression: Parser[For] = "for" ~> rep1sep(listIterator, ",") ~! "return" ~! expression ^^ {
    case iterators ~ _ ~ exp => For(iterators, exp)
  }
  
  private lazy val listIterator = name ~ "in" ~! expression ^^ { case name ~ _ ~ list => (name, list) }
  
  // 47 
  private lazy val ifExpression: Parser[If] = "if" ~> expression ~! "then" ~! expression ~ "else" ~! expression ^^ {
    case condition ~ _ ~ then ~ _ ~ otherwise => If(condition, then, otherwise)
  }
  
  // 48 - TODO should be multiple lists - but how to separate them?
  private lazy val quantifiedExpression: Parser[Exp] = ("some" | "every") ~! listIterator ~! "satisfies" ~! expression ^^ {
  	case "some" ~ Tuple2(name, list) ~ _ ~ condition => SomeItem(name, list, condition)
  	case "every" ~ Tuple2(name, list) ~ _ ~ condition => EveryItem(name, list, condition)
  }
  
  // 49
  private lazy val disjunction: Parser[Exp] = chainl1(expression3, "or" ^^^ Disjunction)
  
  // 50
  private lazy val conjunction: Parser[Exp] = chainl1(expression4, "and" ^^^ Conjunction)
  
  // 51
  private lazy val comparison: Parser[Exp] = simpleComparison |
    expression5 ~ "between" ~! expression5 ~! "and" ~! expression5 ^^ { case x ~ _ ~ a ~ _ ~ b => Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b)) } |
    expression5 ~ "in" ~ "(" ~! positiveUnaryTests <~ ")" ^^ { case x ~ _ ~ _ ~ tests => In(x, tests) } |
    expression5 ~ "in" ~! positiveUnaryTest ^^ { case x ~ _ ~ test => In(x, test) }
      
  private lazy val simpleComparison = expression5 ~ ("<=" | ">=" | "<" | ">" | "!=" | "=") ~! expression5 ^^ {
    case x ~ "=" ~ y => Equal(x, y)
    case x ~ "!=" ~ y => Not(Equal(x, y))
    case x ~ "<" ~ y => LessThan(x, y)
    case x ~ "<=" ~ y => LessOrEqual(x, y)
    case x ~ ">" ~ y => GreaterThan(x, y)
    case x ~ ">=" ~ y => GreaterOrEqual(x, y)
  }
  
  // 53
  private lazy val instanceOf: Parser[InstanceOf] = expression7 ~ "instance" ~! "of" ~! typeName ^^ { case x ~ _ ~ _ ~ typeName => InstanceOf(x, typeName) }
  
  // 54
  private lazy val typeName: Parser[String] = qualifiedName ^^ ( _.mkString(".") )
  
  // 45 - allow nested path expressions
  private lazy val pathExpression: Parser[Exp] = chainl1(expression8, name, "." ^^^ PathExpression )
  
  // 52
  private lazy val filterExpression: Parser[Filter] = expression9 ~ "[" ~! expression <~ "]" ^^ { case list ~ _ ~ filter => Filter(list, filter) } 
  
  // 40
  private lazy val functionInvocation: Parser[Exp] = not(dateTimeLiternal) ~> qualifiedName ~ parameters ^^ { 
    case names ~ params => names match {      
      case name :: Nil => FunctionInvocation(name, params)
      case _ => QualifiedFunctionInvocation(Ref(names), params)
    }}
  
  // 41
  private lazy val parameters: Parser[FunctionParameters] = "(" ~> ")" ^^^ PositionalFunctionParameters(List()) |
    "(" ~> ( namedParameters | positionalParameters ) <~ ")"

  // 42
  private lazy val namedParameters = rep1sep(namedParameter , ",") ^^ ( p => NamedFunctionParameters(p.toMap) )
  
  private lazy val namedParameter = parameterName ~ ":" ~! expression7 ^^ { case name ~ _ ~ value => (name, value) }
  
  // 43 - should be FEEL name
  private lazy val parameterName = name
  
  // 44
  private lazy val positionalParameters = rep1sep(expression7, ",") ^^ ( PositionalFunctionParameters )
  
  // 55
  private lazy val boxedExpression: Parser[Exp] = list | functionDefinition | context
  
  // 56
  private lazy val list: Parser[ConstList] = "[" ~> "]" ^^^ ConstList(List()) |  
  	"[" ~> rep1sep(expression7, ",") <~ "]" ^^ ( ConstList )
  
  // 57
  private lazy val functionDefinition: Parser[FunctionDefinition] = "function" ~! "(" ~> repsep(formalParameter, ",") ~! ")" ~! (externalJavaFunction | expression) ^^ { 
  	case params ~ _ ~ body => FunctionDefinition(params, body) 
  }
  
  private lazy val externalJavaFunction: Parser[JavaFunctionInvocation] = "external" ~ "{" ~ "java" ~ ":" ~ "{" ~> functionClassName ~ "," ~ functionMethodSignature <~ "}" ~ "}" ^^ {  
  	case className ~ _ ~ Tuple2(methodName, arguments) => JavaFunctionInvocation(className, methodName, arguments)
  }
  
  private lazy val functionClassName = "class" ~! ":" ~> stringLiteralWithQuotes 
  
  private lazy val functionMethodSignature = "method_signature" ~! ":" ~! "\"" ~> name ~! "(" ~! repsep(functionMethodArgument, ",") <~ ")" ~! "\"" ^^ { 
  	case methodName ~ _ ~ arguments => (methodName, arguments) 
  }
  
  private lazy val functionMethodArgument = qualifiedName ^^ ( _.mkString(".") )
    
  // 58
  private lazy val formalParameter = parameterName
  
  // 59
  private lazy val context: Parser[ConstContext] = "{" ~> repsep(contextEntry, ",") <~ "}" ^^ ( ConstContext )
  
  // 60 
  private lazy val contextEntry = key ~ ":" ~! expression ^^ { case key ~ _ ~ value => (key -> value) }
  
  // 61
  private lazy val key = name | stringLiteralWithQuotes
  
}