package org.camunda.feel.spi

import org.camunda.feel.interpreter._

/**
  * Override this class if you want to implement a custom Context.
  * Call the corresponding super method to handle the default/error case.
  * Typically you will just override one of the dynamic providers (variableProvider, functionProvider)
  */
abstract class CustomContext extends ContextBase {

  override def variableProvider: VariableProvider =
    VariableProvider.EmptyVariableProvider

  override def functionProvider: FunctionProvider =
    FunctionProvider.EmptyFunctionProvider

}
