package org.camunda.feel

import org.camunda.feel.FeelEngine.{
  EvalExpressionResult,
  EvalUnaryTestsResult,
  Failure
}
import org.camunda.feel.interpreter.ValueMapper.CompositeValueMapper
import org.camunda.feel.interpreter.{DefaultValueMapper, FeelInterpreter, _}
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.parser.{Exp, FeelParser}
import org.camunda.feel.spi.CustomValueMapper

import scala.collection.JavaConverters._

object FeelEngine {

  type EvalExpressionResult = Either[Failure, Any]
  type EvalUnaryTestsResult = Either[Failure, Boolean]

  def defaultFunctionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

  def defaultValueMapper: ValueMapper = ValueMapper.defaultValueMapper

  case class Failure(message: String)

  class Builder {

    private var functionProvider_ : FunctionProvider = defaultFunctionProvider

    private var valueMapper_ : ValueMapper = defaultValueMapper
    private var customValueMappers_ : List[CustomValueMapper] = List.empty

    def functionProvider(functionProvider: FunctionProvider): Builder = {
      functionProvider_ = functionProvider
      this
    }

    def customValueMapper(customValueMapper: CustomValueMapper): Builder = {
      customValueMappers_ = customValueMapper :: customValueMappers_
      valueMapper_ = CompositeValueMapper(customValueMappers_)
      this
    }

    def valueMapper(valueMapper: ValueMapper): Builder = {
      valueMapper_ = valueMapper
      this
    }

    def build: FeelEngine = new FeelEngine(
      functionProvider = functionProvider_,
      valueMapper = valueMapper_
    )

  }

  object UnaryTests {
    val inputVariable: String = "inputVariableName"
    val defaultInputVariable: String = "cellInput"
  }

}

class FeelEngine(val functionProvider: FunctionProvider =
                   FeelEngine.defaultFunctionProvider,
                 val valueMapper: ValueMapper = FeelEngine.defaultValueMapper) {

  val interpreter = new FeelInterpreter

  logger.info(
    s"Engine created. [value-mapper: $valueMapper, function-provider: $functionProvider]")

  private val rootContext: EvalContext = new EvalContext(
    valueMapper = valueMapper,
    variableProvider = VariableProvider.EmptyVariableProvider,
    functionProvider = FunctionProvider.CompositeFunctionProvider(
      List(BuiltinFunctions, functionProvider))
  )

  def evalExpression(
      expression: String,
      variables: java.util.Map[String, Object]): EvalExpressionResult =
    evalExpression(expression, variables.asScala.toMap)

  def evalExpression(
      expression: String,
      variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(FeelParser.parseExpression,
         expression,
         Context.StaticContext(variables))
  }

  def evalExpression(expression: String,
                     context: Context): EvalExpressionResult = {
    eval(FeelParser.parseExpression, expression, context)
  }

  def evalUnaryTests(
      expression: String,
      variables: java.util.Map[String, Object]): EvalUnaryTestsResult =
    evalUnaryTests(expression, variables.asScala.toMap)

  def evalUnaryTests(
      expression: String,
      variables: Map[String, Any] = Map()): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests,
         expression,
         Context.StaticContext(variables))
      .map(value => value.asInstanceOf[Boolean])
  }

  def evalUnaryTests(expression: String,
                     context: Context): EvalUnaryTestsResult = {
    eval(FeelParser.parseUnaryTests, expression, context)
      .map(value => value.asInstanceOf[Boolean])
  }

  private def eval(parser: String => ParseResult[Exp],
                   expression: String,
                   context: Context): EvalExpressionResult =
    parse(parser, expression)
      .flatMap(expr => eval(expr, context))

  private def parse(parser: String => ParseResult[Exp],
                    expression: String): Either[Failure, ParsedExpression] =
    parser(expression) match {
      case Success(exp, _) => Right(ParsedExpression(exp, expression))
      case e: NoSuccess =>
        Left(Failure(s"failed to parse expression '$expression': $e"))
    }

  def eval(exp: ParsedExpression, context: Context): EvalExpressionResult =
    eval(exp, rootContext + context)

  private def eval(exp: ParsedExpression,
                   context: EvalContext): EvalExpressionResult = {
    interpreter.eval(exp.expression)(context) match {
      case ValError(cause) =>
        Left(Failure(s"failed to evaluate expression '${exp.text}': $cause"))
      case value => Right(valueMapper.unpackVal(value))
    }
  }

  def eval(exp: ParsedExpression,
           variables: java.util.Map[String, Object]): EvalExpressionResult =
    eval(exp, variables.asScala.toMap)

  def eval(exp: ParsedExpression,
           variables: Map[String, Any] = Map()): EvalExpressionResult = {
    eval(exp, Context.StaticContext(variables))
  }

  def parseExpression(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseExpression, expression)

  def parseUnaryTests(expression: String): Either[Failure, ParsedExpression] =
    parse(FeelParser.parseUnaryTests, expression)

}
