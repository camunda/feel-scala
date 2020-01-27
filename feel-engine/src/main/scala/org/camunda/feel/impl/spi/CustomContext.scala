package org.camunda.feel.impl.spi

import org.camunda.feel.interpreter.impl._
import org.camunda.feel.interpreter.FunctionProvider
import org.camunda.feel.interpreter.impl.{Context, VariableProvider}

/**
  * Override this class if you want to implement a custom Context.
  * Call the corresponding super method to handle the default/error case.
  * Typically you will just override one of the dynamic providers (variableProvider, functionProvider)
  */
abstract class CustomContext extends Context {

  override def variableProvider: VariableProvider =
    VariableProvider.EmptyVariableProvider

  override def functionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

}
