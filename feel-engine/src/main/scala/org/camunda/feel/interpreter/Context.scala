package org.camunda.feel.interpreter

/**
  * A Context provides access to the variables/fields and functions/methods in the scope represented by this Context.
  */
trait Context {

  def valueMapper: ValueMapper

  def variable(name: String): Val

  def function(name: String, paramCount: Int): Val

  def function(name: String, parameters: Set[String]): Val

}
