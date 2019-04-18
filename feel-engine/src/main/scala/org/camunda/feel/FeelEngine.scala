package org.camunda.feel

import org.camunda.feel.FeelEngine.{EvalExpressionResult, EvalUnaryTestsResult, Failure}
import org.camunda.feel.interpreter.CompositeContext._
import org.camunda.feel.interpreter.{DefaultValueMapper, FeelInterpreter, _}
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.parser.{Exp, FeelParser}

import scala.collection.JavaConverters._

object FeelEngine {

  type EvalExpressionResult = Either[Failure, Any]
  type EvalUnaryTestsResult = Either[Failure, Boolean]

  def defaultFunctionProvider: FunctionProvider = FunctionProvider.EmptyFunctionProvider

  def defaultValueMapper: ValueMapper = DefaultValueMapper.instance

  case class Failure(message: String)

  class Builder {

    private var functionProvider_ : FunctionProvider = defaultFunctionProvider
    private var valueMapper_ : ValueMapper = defaultValueMapper

    def functionProvider(functionProvider: FunctionProvider): Builder = {
      functionProvider_ = functionProvider
      this
    }

    def valueMapper(valueMapper: ValueMapper): Builder = {
      valueMapper_ = valueMapper
      this
    }

    def build: FeelEngine = new FeelEngine(
      functionProvider = functionProvider_,
      valueMapper = valueMapper_)

  }

}

/**
  * @author Philipp Ossler
  */
class FeelEngine(val functionProvider: FunctionProvider = FeelEngine.defaultFunctionProvider,
                 val valueMapper: ValueMapper = FeelEngine.defaultValueMapper) {

  val interpreter = new FeelInterpreter

  def this() = this(
    functionProvider = FunctionProvider.EmptyFunctionProvider,
    valueMapper = DefaultValueMapper.instance
  )

  logger.info(s"Engine created. [value-mapper: $valueMapper, function-provider: $functionProvider]")

  def evalExpression(expression: String,
                     variables: java.util.Map[String, Object]): EvalExpressionResult =
    evalExpression(expression, variables.asScala.toMap)

  def evalExpression(expression: String,
                     variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(FeelParser.parseExpression, expression, rootContext(variables))
  }

  def evalExpression(expression: String, context: Context): EvalExpressionResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalUnaryTests(expression: String,
                     variables: java.util.Map[String, Object]): EvalUnaryTestsResult =
    evalUnaryTests(expression, variables.asScala.toMap)

  def evalUnaryTests(expression: String,
                     variables: Map[String, Any] = Map()): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests, expression, rootContext(variables))
      .right
      .map(value => value.asInstanceOf[Boolean])
  }

  private def rootContext(variables: Map[String, Any]) =
    RootContext(variables = variables,
      functionProvider = functionProvider,
      valueMapper = valueMapper)

  def evalUnaryTests(expression: String, context: Context): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests, expression, context)
      .right
      .map(value => value.asInstanceOf[Boolean])
  }

  private def eval(parser: String => ParseResult[Exp],
                   expression: String,
                   context: Context): EvalExpressionResult =
    parse(parser, expression)
      .right
      .flatMap(expr => eval(expr, context))

  private def parse(parser: String => ParseResult[Exp],
                    expression: String): Either[Failure, ParsedExpression] = parser(expression) match {
    case Success(exp, _) => Right(ParsedExpression(exp, expression))
    case e: NoSuccess => Left(Failure(s"failed to parse expression '$expression': $e"))
  }

  def eval(exp: ParsedExpression, context: Context): EvalExpressionResult = {
    interpreter.eval(exp.expression)(rootContext(context)) match {
      case ValError(cause) =>
        Left(Failure(s"failed to evaluate expression '${exp.text}': $cause"))
      case value => Right(valueMapper.unpackVal(value))
    }
  }

  private def rootContext(context: Context): Context = {
    context match {
      case c: RootContext => c
      case c: DefaultContext =>
        val fp =
          if (c.functionProvider == FunctionProvider.EmptyFunctionProvider) {
            functionProvider
          } else if (functionProvider == FunctionProvider.EmptyFunctionProvider) {
            c.functionProvider
          } else {
            new FunctionProvider.CompositeFunctionProvider(
              List(functionProvider, c.functionProvider))
          }

        RootContext(
          variables = c.variables,
          additionalFunctions = c.functions,
          variableProvider = c.variableProvider,
          functionProvider = fp,
          valueMapper = valueMapper
        )
      case c =>
        RootContext(functionProvider = functionProvider,
          valueMapper = valueMapper) + c
    }
  }

  def eval(exp: ParsedExpression,
           variables: java.util.Map[String, Object]): EvalExpressionResult =
    eval(exp, variables.asScala.toMap)

  def eval(exp: ParsedExpression,
           variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(exp, rootContext(variables))
  }

  def parseExpression(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseExpression, expression)

  def parseUnaryTests(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseUnaryTests, expression)

}
