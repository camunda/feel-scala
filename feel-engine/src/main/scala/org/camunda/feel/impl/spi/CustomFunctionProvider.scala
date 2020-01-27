package org.camunda.feel.impl.spi

import org.camunda.feel.interpreter.impl._
import org.camunda.feel.interpreter.FunctionProvider
import org.camunda.feel.interpreter.impl.ValFunction

/**
  * Provides one or more functions which can be used in an expression.
  */
trait CustomFunctionProvider extends FunctionProvider {

  /**
    * Returns a list of functions for the given name. There can be multiple functions with different parameters.
    *
    * @param name the name of the function
    * @return a list of functions or an empty list, if no function is provided for this name
    */
  override def getFunctions(name: String): List[ValFunction] =
    getFunction(name)
      .map(List(_))
      .getOrElse(List.empty)

  /**
    * Returns the function for the given name.
    *
    * @param name the name of the function
    * @return the function or [[None]], if no function is provided for this name
    */
  def getFunction(name: String): Option[ValFunction]

}
