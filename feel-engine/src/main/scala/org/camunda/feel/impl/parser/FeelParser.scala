package org.camunda.feel.impl.parser

import org.camunda.feel.syntaxtree._
import org.camunda.feel._

import scala.util.Try
import scala.util.parsing.combinator.JavaTokenParsers

object FeelParser extends JavaTokenParsers {

  def parseExpression(exp: String): ParseResult[Exp] = parseExp(expression, exp)

  def parseUnaryTests(expression: String): ParseResult[Exp] =
    parseExp(unaryTests, expression)

  private def parseExp[T](parser: Parser[T], exp: String) =
    parseAll(parser, exp)

  // override to ignore comment '// ...' and '/* ... */'
  protected override val whiteSpace =
    """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  private lazy val reservedWord: Parser[String] = ("null\\b".r
    | "true\\b".r | "false\\b".r
    | "function\\b".r
    | "if\\b".r | "then\\b".r | "else\\b".r
    | "for\\b".r
    | "between\\b".r
    | "instance\\b".r | "of\\b".r)

  // list of built-in function names with whitespaces
  // -- other names match the 'function name' pattern
  private lazy val builtinFunctionName: Parser[String] = ("date and time"
    | "years and months duration"
    | "string length"
    | "upper case"
    | "lower case"
    | "substring before"
    | "substring after"
    | "starts with"
    | "ends with"
    | "list contains"
    | "insert before"
    | "index of"
    | "distinct values"
    | "get entries"
    | "get value")

  // list of built-in function parameter names with whitespaces
  // -- other names match the 'parameter name' pattern
  private lazy val builtinFunctionParameterNames
    : Parser[String] = ("start position"
    | "grouping separator"
    | "decimal separator")

  private lazy val identifier = not(reservedWord) ~> ident

  // Java-like string literal: ("\""+"""([^"\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*"""+"\"")
  // modification: allow '\'
  private lazy val stringLiteralWithQuotes: Parser[String] =
    ("\"" + """([^"\x00-\x1F\x7F]|\\u[a-fA-F0-9]{4})*""" + "\"").r ^^ (_.replaceAll(
      "\"",
      ""))

  // 1 a)
  private lazy val expression: Parser[Exp] = textualExpression
  // 1 b)
  private lazy val expression10 = boxedExpression

  // 3
  private lazy val textualExpressions: Parser[ConstList] = rep1sep(
    textualExpression,
    ",") ^^ ConstList

  // 2 a)
  private lazy val textualExpression
    : Parser[Exp] = functionDefinition | forExpression | ifExpression | quantifiedExpression | expression2
  // 2b)
  private lazy val expression2 = disjunction
  // 2 c)
  private lazy val expression3 = conjunction
  // 2 d)
  private lazy val expression4: Parser[Exp] = expression5 >> optionalComparison
  // 2 e)
  private lazy val expression5 = arithmeticExpression
  // 2 f)
  private lazy val expression6 = expression7 >> (x =>
    instanceOf.? ^^ (_.fold(x)(InstanceOf(x, _))))
  // 2 g)
  private lazy val expression7 = pathExpression
  // 2 h)
  private lazy val expression8 = functionInvocation | builtinFunctionInvocation | filteredExpression9
  // 2 i)
  private lazy val expression9 =
    literal |
      name ^^ (n => Ref(List(n))) |
      "?" ^^^ ConstInputValue |
      simplePositiveUnaryTest |
      "(" ~> textualExpression <~ ")" |
      expression10

  // 6
  private lazy val simpleExpressions: Parser[ConstList] = rep1sep(
    simpleExpression,
    ",") ^^ ConstList

  // 5
  private lazy val simpleExpression
    : Parser[Exp] = arithmeticExpression | simpleValue

  // 4 a) -> 21+22
  private lazy val arithmeticExpression =
    chainl1(arithmeticExpression2, "+" ^^^ Addition | "-" ^^^ Subtraction)
  // 4 b) -> 23+24
  private lazy val arithmeticExpression2 =
    chainl1(arithmeticExpression3, "*" ^^^ Multiplication | "/" ^^^ Division)
  // 4 c) -> 25
  private lazy val arithmeticExpression3 =
    chainl1(arithmeticExpression4, "**" ^^^ Exponentiation)
  // 4 d) -> 26
  private lazy val arithmeticExpression4 = opt("-") ~ expression6 ^^ {
    case Some(_) ~ e => ArithmeticNegation(e)
    case None ~ e    => e
  }

  // 17
  private lazy val unaryTests: Parser[Exp] =
    "not" ~! "(" ~> positiveUnaryTests <~ ")" ^^ Not |
      positiveUnaryTests |
      "-" ^^^ ConstBool(true)

  // 16
  private lazy val positiveUnaryTests: Parser[Exp] = rep1sep(positiveUnaryTest,
                                                             ",") ^^ {
    case test :: Nil => test
    case tests       => AtLeastOne(tests)
  }

  // 15 - in DMN 1.2 it's only 'expression' (which also covers simple positive unary test)
  //    - however, parse simple positive unary test first since this is most usual
  private lazy val positiveUnaryTest: Parser[Exp] = (
    "null" ^^^ InputEqualTo(ConstNull) |
      simplePositiveUnaryTest |
      expression ^^ (UnaryTestExpression(_))
  )

  // 14
  private lazy val simpleUnaryTests: Parser[Exp] =
    "-" ^^^ ConstBool(true) |
      "not" ~! "(" ~> simplePositiveUnaryTests <~ ")" ^^ Not |
      simplePositiveUnaryTests

  // 13
  private lazy val simplePositiveUnaryTests: Parser[Exp] = rep1sep(
    simplePositiveUnaryTest,
    ",") ^^ {
    case test :: Nil => test
    case tests       => AtLeastOne(tests)
  }

  // 7
  private lazy val simplePositiveUnaryTest: Parser[Exp] =
    "<" ~> endpoint ^^ InputLessThan |
      "<=" ~> endpoint ^^ InputLessOrEqual |
      ">" ~> endpoint ^^ InputGreaterThan |
      ">=" ~> endpoint ^^ InputGreaterOrEqual |
      interval |
      simpleValue ^^ InputEqualTo

  // 18 - allow any expression as endpoint
  private lazy val endpoint: Parser[Exp] = simpleValue | expression

  // 19 - need to exclude function invocation from qualified name
  private lazy val simpleValue =
    simpleLiteral |
      not(""".+\(.*\)""".r) ~> qualifiedName ^^ (Ref(_)) |
      "?" ^^^ ConstInputValue

  // 33
  private lazy val literal: Parser[Exp] = "null" ^^^ ConstNull | simpleLiteral

  // 34
  private lazy val simpleLiteral = booleanLiteral | dateTimeLiteral | stringLiteraL | numericLiteral

  // 36
  private lazy val booleanLiteral: Parser[ConstBool] = "true" ^^^ ConstBool(
    true) | "false" ^^^ ConstBool(false)

  // 62
  private lazy val dateTimeLiteral: Parser[Exp] =
    "date" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ parseDate |
      "time" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ parseTime |
      "date and time" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ parseDateTime |
      "duration" ~ "(" ~> stringLiteralWithQuotes <~ ")" ^^ parseDuration |
      failure("expected date time literal")

  // 35 -
  private lazy val stringLiteraL
    : Parser[ConstString] = stringLiteralWithQuotes ^^ ConstString

  // 37 - use combined regex instead of multiple parsers
  private lazy val numericLiteral: Parser[ConstNumber] =
    """(-?(\d+(\.\d+)?|\d*\.\d+))""".r ^^ (ConstNumber(_))

  // 39
  private lazy val digits: Parser[String] = rep1(digit) ^^ (_.mkString)

  // 38
  private lazy val digit: Parser[String] = "[0-9]".r

  // 20
  private lazy val qualifiedName: Parser[List[String]] = rep1sep(name, ".")

  // 27 - simplified name definition
  private lazy val name
    : Parser[String] = escapedIdentifier | "time offset" | identifier

  private lazy val escapedIdentifier
    : Parser[String] = ("`" ~> """([^`"\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*""".r <~ "`")

  // FEEL name definition
  private lazy val feelName: Parser[String] = nameStart ~! rep(
    namePart | additionalNameSymbols) ^^ { case s ~ ps => s + ps.mkString }

  // 28
  private lazy val nameStart = nameStartChar ~! rep(namePartChar) ^^ {
    case s ~ ps => s + ps.mkString
  }

  // 29
  private lazy val namePart = rep1(namePartChar) ^^ (_.mkString)

  // 30- unknown unicode chars "[\\uC0-\\uD6]".r | "[\\uD8-\\uF6]".r | "[\\uF8-\\u2FF]".r | "[\\u370-\\u37D]".r | "[\\u37F-\u1FFF]".r
  private lazy val nameStartChar = "?" | "[A-Z]".r | "_" | "[a-z]".r |
    "[\u200C-\u200D]".r | "[\u2070-\u218F]".r | "[\u2C00-\u2FEF]".r | "[\u3001-\uD7FF]".r | "[\uF900-\uFDCF]".r | "[\uFDF0-\uFFFD]".r | "[\u10000-\uEFFFF]".r

  // 31 - unknown unicode char "[\\uB7]".r
  private lazy val namePartChar = nameStartChar | digit | "[\u0300-\u036F]".r | "[\u203F-\u2040]".r

  // 32
  private lazy val additionalNameSymbols = "." | "/" | "-" | "’" | "+" | "*"

  // 8
  private lazy val interval
    : Parser[Interval] = (openIntervalStart | closedIntervalStart) ~ endpoint ~ ".." ~! endpoint ~! (openIntervalEnd | closedIntervalEnd) ^^ {
    case ("(" | "]") ~ start ~ _ ~ end ~ (")" | "[") =>
      syntaxtree.Interval(OpenIntervalBoundary(start),
                          OpenIntervalBoundary(end))
    case ("(" | "]") ~ start ~ _ ~ end ~ "]" =>
      syntaxtree.Interval(OpenIntervalBoundary(start),
                          ClosedIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ (")" | "[") =>
      syntaxtree.Interval(ClosedIntervalBoundary(start),
                          OpenIntervalBoundary(end))
    case "[" ~ start ~ _ ~ end ~ "]" =>
      syntaxtree.Interval(ClosedIntervalBoundary(start),
                          ClosedIntervalBoundary(end))
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
  private lazy val forExpression: Parser[For] = "for" ~> rep1sep(
    listIterator,
    ",") ~! "return" ~! expression ^^ {
    case iterators ~ _ ~ exp => syntaxtree.For(iterators, exp)
  }

  private lazy val listIterator = name ~ "in" ~! (range | expression) ^^ {
    case name ~ _ ~ list => (name, list)
  }

  private lazy val range
    : Parser[syntaxtree.Range] = expression ~ ".." ~ expression ^^ {
    case start ~ _ ~ end => syntaxtree.Range(start, end)
  }

  // 47
  private lazy val ifExpression
    : Parser[If] = "if" ~> expression ~! "then" ~! expression ~ "else" ~! expression ^^ {
    case condition ~ _ ~ statement ~ _ ~ elseStatement =>
      If(condition, statement, elseStatement)
  }

  // 48 - no separator in spec grammar but in examples
  private lazy val quantifiedExpression: Parser[Exp] = ("some" | "every") ~! rep1sep(
    listIterator,
    ",") ~! "satisfies" ~! expression ^^ {
    case "some" ~ iterators ~ _ ~ condition  => SomeItem(iterators, condition)
    case "every" ~ iterators ~ _ ~ condition => EveryItem(iterators, condition)
  }

  // 49
  private lazy val disjunction: Parser[Exp] =
    chainl1(expression3, "or" ^^^ Disjunction)

  // 50
  private lazy val conjunction: Parser[Exp] =
    chainl1(expression4, "and" ^^^ Conjunction)

  // 51
  private lazy val optionalComparison: Exp => Parser[Exp] = (x: Exp) => {
    simpleComparison(x) |
      "between" ~! expression5 ~! "and" ~! expression5 ^^ {
        case _ ~ a ~ _ ~ b =>
          Conjunction(GreaterOrEqual(x, a), LessOrEqual(x, b))
      } |
      "in" ~ "(" ~! positiveUnaryTests <~ ")" ^^ {
        case _ ~ _ ~ tests => In(x, tests)
      } |
      "in" ~! positiveUnaryTest ^^ { case _ ~ test => In(x, test) } |
      success(x) // no comparison
  }

  private lazy val simpleComparison = (x: Exp) =>
    ("<=" | ">=" | "<" | ">" | "!=" | "=") ~! expression5 ^^ {
      case "=" ~ y  => Equal(x, y)
      case "!=" ~ y => Not(Equal(x, y))
      case "<" ~ y  => LessThan(x, y)
      case "<=" ~ y => LessOrEqual(x, y)
      case ">" ~ y  => GreaterThan(x, y)
      case ">=" ~ y => GreaterOrEqual(x, y)
  }

  // 53
  private lazy val instanceOf
    : Parser[String] = "instance" ~! "of" ~! typeName ^^ {
    case _ ~ _ ~ typeName => typeName
  }

  // 54
  private lazy val typeName: Parser[String] = qualifiedName ^^ (_.mkString("."))

  // 45 - allow nested path expressions
  private lazy val pathExpression: Parser[Exp] = chainl1(
    expression8,
    name,
    "." ^^^ PathExpression) ~ opt("[" ~> expression <~ "]") ^^ {
    case path ~ None         => path
    case path ~ Some(filter) => Filter(path, filter)
  }

  // 52
  private lazy val filteredExpression9
    : Parser[Exp] = expression9 ~ ("[" ~! expression <~ "]").? ^^ {
    case list ~ Some(_ ~ filter) => Filter(list, filter)
    case list ~ None             => list
  }

  // 40
  private lazy val functionInvocation
    : Parser[Exp] = not(dateTimeLiteral) ~> qualifiedName ~ parameters ^^ {
    case names ~ params =>
      names match {
        case name :: Nil => FunctionInvocation(name, params)
        case _ =>
          QualifiedFunctionInvocation(Ref(names.dropRight(1)),
                                      names.last,
                                      params)
      }
  }

  private lazy val builtinFunctionInvocation
    : Parser[Exp] = not(dateTimeLiteral) ~> builtinFunctionName ~ parameters ^^ {
    case name ~ params => syntaxtree.FunctionInvocation(name, params)
  }

  // 41
  private lazy val parameters
    : Parser[FunctionParameters] = "(" ~> ")" ^^^ PositionalFunctionParameters(
    List()) |
    "(" ~> (namedParameters | positionalParameters) <~ ")"

  // 42
  private lazy val namedParameters = rep1sep(namedParameter, ",") ^^ (p =>
    NamedFunctionParameters(p.toMap))

  private lazy val namedParameter = parameterName ~ ":" ~! expression ^^ {
    case name ~ _ ~ value => (name, value)
  }

  // 43 - should be FEEL name
  private lazy val parameterName = builtinFunctionParameterNames | name

  // 44
  private lazy val positionalParameters = rep1sep(expression, ",") ^^ (PositionalFunctionParameters)

  // 55
  private lazy val boxedExpression
    : Parser[Exp] = list | functionDefinition | context

  // 56
  private lazy val list: Parser[ConstList] = "[" ~> "]" ^^^ ConstList(List()) |
    "[" ~> rep1sep(expression7, ",") <~ "]" ^^ (ConstList)

  // 57
  private lazy val functionDefinition
    : Parser[FunctionDefinition] = "function" ~! "(" ~> repsep(
    formalParameter,
    ",") ~! ")" ~! (externalJavaFunction | expression) ^^ {
    case params ~ _ ~ body => FunctionDefinition(params, body)
  }

  private lazy val externalJavaFunction
    : Parser[JavaFunctionInvocation] = "external" ~ "{" ~ "java" ~ ":" ~ "{" ~> functionClassName ~ "," ~ functionMethodSignature <~ "}" ~ "}" ^^ {
    case className ~ _ ~ Tuple2(methodName, arguments) =>
      JavaFunctionInvocation(className, methodName, arguments)
  }

  private lazy val functionClassName = "class" ~! ":" ~> stringLiteralWithQuotes

  private lazy val functionMethodSignature = "method signature" ~! ":" ~! "\"" ~> name ~! "(" ~! repsep(
    functionMethodArgument,
    ",") <~ ")" ~! "\"" ^^ {
    case methodName ~ _ ~ arguments => (methodName, arguments)
  }

  private lazy val functionMethodArgument = qualifiedName ^^ (_.mkString("."))

  // 58
  private lazy val formalParameter = parameterName

  // 59
  private lazy val context: Parser[ConstContext] = "{" ~> repsep(
    contextEntry,
    ",") <~ "}" ^^ (ConstContext)

  // 60
  private lazy val contextEntry = key ~ ":" ~! expression ^^ {
    case key ~ _ ~ value => (key -> value)
  }

  // 61
  private lazy val key = name | stringLiteralWithQuotes

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
