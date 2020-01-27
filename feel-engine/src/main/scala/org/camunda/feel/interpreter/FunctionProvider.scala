package org.camunda.feel.interpreter

import org.camunda.feel.interpreter.impl.ValFunction

import scala.collection.mutable

trait FunctionProvider {

  def getFunctions(name: String): List[ValFunction]

  def functionNames: Iterable[String]

  def getFunctions(): Map[String, List[ValFunction]] =
    functionNames
      .map(name => name -> getFunctions(name))
      .toMap

}

object FunctionProvider {

  object EmptyFunctionProvider extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] = List.empty

    override def functionNames: Iterable[String] = List.empty
  }

  case class StaticFunctionProvider(functions: Map[String, List[ValFunction]])
      extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] =
      functions.getOrElse(name, List.empty)

    override def functionNames: Iterable[String] = functions.keys
  }

  case class CacheFunctionProvider(provider: FunctionProvider)
      extends FunctionProvider {

    private val cache: mutable.Map[String, List[ValFunction]] =
      mutable.Map.empty

    override def getFunctions(name: String): List[ValFunction] =
      cache.getOrElseUpdate(name, provider.getFunctions(name))

    override def functionNames: Iterable[String] =
      cache.keys ++ provider.functionNames
  }

  case class CompositeFunctionProvider(providers: List[FunctionProvider])
      extends FunctionProvider {

    override def getFunctions(name: String): List[ValFunction] =
      providers.foldLeft(List[ValFunction]())((functions, provider) =>
        functions ++ provider.getFunctions(name))

    override def functionNames: Iterable[String] =
      providers.flatMap(_.functionNames)
  }

}
