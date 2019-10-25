package org.camunda.feel.interpreter

import scala.collection.mutable

trait VariableProvider {

  def getVariable(name: String): Option[Any]

  def keys: Iterable[String]

  def getVariables: Map[String, Any] =
    keys
      .map(key => key -> getVariable(key).getOrElse(None))
      .toMap

}

object VariableProvider {

  object EmptyVariableProvider extends VariableProvider {

    override def getVariable(name: String): Option[Val] = None

    override def keys: Iterable[String] = List.empty
  }

  case class StaticVariableProvider(variables: Map[String, Any])
      extends VariableProvider {

    override def getVariable(name: String): Option[Any] = variables.get(name)

    override def keys: Iterable[String] = variables.keys

  }

  case class CacheVariableProvider(provider: VariableProvider)
      extends VariableProvider {

    private val cache: mutable.Map[String, Any] = mutable.Map.empty

    override def getVariable(name: String): Option[Any] =
      cache.get(name) match {
        case Some(value) => Some(value)
        case None =>
          provider.getVariable(name) match {
            case Some(value) => cache.put(name, value); Some(value)
            case None        => None
          }
      }

    override def keys: Iterable[String] = cache.keys ++ provider.keys

  }

  case class CompositeVariableProvider(providers: List[VariableProvider])
      extends VariableProvider {

    override def getVariable(name: String): Option[Any] = {
      for (provider <- providers) {
        provider.getVariable(name) match {
          case Some(v) => return Some(v)
          case _       =>
        }
      }
      None
    }

    override def keys: Iterable[String] = providers.flatMap(_.keys)
  }

}
