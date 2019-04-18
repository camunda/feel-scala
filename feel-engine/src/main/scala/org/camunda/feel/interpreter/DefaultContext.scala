package org.camunda.feel.interpreter

import org.camunda.feel.spi._

import scala.collection.mutable

abstract class ContextBase extends Context {

  override def valueMapper: ValueMapper = DefaultValueMapper.instance

  def variables: Map[String, Any] = Map.empty

  private val cachedVariables: mutable.Map[String, Any] = mutable.Map.empty

  def variableProvider: VariableProvider =
    VariableProvider.EmptyVariableProvider

  def functions: Map[String, List[ValFunction]] = Map.empty

  private val cachedFunctions: mutable.Map[String, List[ValFunction]] =
    mutable.Map.empty

  def functionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

  override def variable(name: String): Val =
    variables.get(name) orElse cachedVariables.get(name) match {
      case Some(x) => valueMapper.toVal(x)
      case None =>
        variableProvider.getVariable(name) match {
          case Some(v) => cachedVariables.put(name, v); v
          case None    => ValError(s"no variable found for name '$name'")
        }
    }

  override def function(name: String, paramCount: Int): Val =
    findFunction(functions.getOrElse(name, List.empty), paramCount) orElse findFunction(
      cachedFunctions.getOrElse(name, List.empty),
      paramCount) match {
      case Some(f: ValFunction) => f
      case _ => {
        val providerFunctions = functionProvider.getFunctions(name)

        val functionsByName = cachedFunctions getOrElse (name, List.empty)
        cachedFunctions.put(name, functionsByName ++ providerFunctions)

        findFunction(providerFunctions, paramCount) getOrElse {
          ValError(
            s"no function found with name '$name' and $paramCount parameters")
        }
      }
    }

  override def function(name: String, params: Set[String]): Val =
    findFunction(functions.getOrElse(name, List.empty), params) orElse findFunction(
      cachedFunctions.getOrElse(name, List.empty),
      params) match {
      case Some(f: ValFunction) => f
      case _ => {
        val providerFunctions = functionProvider.getFunctions(name)

        val functionsByName = cachedFunctions getOrElse (name, List.empty)
        cachedFunctions.put(name, functionsByName ++ providerFunctions)

        findFunction(providerFunctions, params) getOrElse {
          ValError(
            s"no function found with name '$name' and parameters: ${params.mkString(",")}")
        }
      }
    }

  private def findFunction(functions: List[ValFunction],
                           paramCount: Int): Option[ValFunction] =
    functions.find(f =>
      f.params.size == paramCount || (f.params.size < paramCount && f.hasVarArgs))

  private def findFunction(functions: List[ValFunction],
                           params: Set[String]): Option[ValFunction] =
    functions.find(f => f.paramSet == params || params.subsetOf(f.paramSet))

}

case class CompositeContext(contexts: Seq[Context],
                            override val valueMapper: ValueMapper)
    extends Context {

  override def variable(name: String): Val = {
    for (context <- contexts) {
      context.variable(name) match {
        case _: ValError =>
        case x: Val      => return x
      }
    }
    ValError(s"no variable found for name '$name'")
  }

  override def function(name: String, paramCount: Int): Val = {
    for (context <- contexts) {
      context.function(name, paramCount) match {
        case _: ValError    =>
        case f: ValFunction => return f
        case _: Val         =>
      }
    }
    ValError(s"no function found with name '$name' and $paramCount parameters")
  }

  override def function(name: String, params: Set[String]): Val = {
    for (context <- contexts) {
      context.function(name, params) match {
        case _: ValError    =>
        case f: ValFunction => return f
        case _: Val         =>
      }
    }
    ValError(
      s"no function found with name '$name' and parameters: ${params.mkString(",")}")
  }

}

object CompositeContext {

  implicit class ContextComposition(ctx: Context) {

    def +(c: Context): Context = {
      CompositeContext(Seq(c, ctx), ctx.valueMapper)
    }

    def +(v: (String, Any)): Context = {
      CompositeContext(Seq(DefaultContext(Map(v)), ctx), ctx.valueMapper)
    }

    def ++(v: Map[String, Any]): Context = {
      CompositeContext(Seq(DefaultContext(v), ctx), ctx.valueMapper)
    }
  }
}

case class DefaultContext(
    override val variables: Map[String, Any] = Map.empty,
    override val functions: Map[String, List[ValFunction]] = Map.empty,
    override val variableProvider: VariableProvider =
      VariableProvider.EmptyVariableProvider,
    override val functionProvider: FunctionProvider =
      FunctionProvider.EmptyFunctionProvider,
    override val valueMapper: ValueMapper = DefaultValueMapper.instance
) extends ContextBase

case class RootContext(
    override val variables: Map[String, Any] = Map.empty,
    additionalFunctions: Map[String, List[ValFunction]] = Map.empty,
    override val variableProvider: VariableProvider =
      VariableProvider.EmptyVariableProvider,
    override val functionProvider: FunctionProvider =
      FunctionProvider.EmptyFunctionProvider,
    override val valueMapper: ValueMapper = DefaultValueMapper.instance
) extends ContextBase {

  override val functions = BuiltinFunctions.functions ++ additionalFunctions
}

object RootContext {

  val defaultInputVariable: String = "cellInput"

  val inputVariableKey: String = "inputVariableName"

  def empty: RootContext = RootContext()

}
