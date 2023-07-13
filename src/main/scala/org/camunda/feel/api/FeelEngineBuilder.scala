package org.camunda.feel.api

import org.camunda.feel.{FeelEngine, FeelEngineClock}
import org.camunda.feel.FeelEngine.{Configuration, defaultFunctionProvider}
import org.camunda.feel.context.FunctionProvider
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper

/**
  * Builds a new instance of the FEEL engine. Use the setters to customize the engine.
  */
case class FeelEngineBuilder private(
                            functionProvider: FunctionProvider = defaultFunctionProvider,
                            valueMapper: ValueMapper = FeelEngine.defaultValueMapper,
                            customValueMappers: List[CustomValueMapper] = List.empty,
                            clock: FeelEngineClock = FeelEngine.defaultClock,
                            configuration: Configuration = FeelEngine.defaultConfiguration
                          ) {

  /**
    * Sets the given [[FunctionProvider]] for the engine.
    */
  def withFunctionProvider(functionProvider: FunctionProvider): FeelEngineBuilder =
    copy(functionProvider = functionProvider)

  /**
    * Sets the given [[CustomValueMapper]] for the engine. Can be called multiple times to chain the
    * value mappers.
    */
  def withCustomValueMapper(customValueMapper: CustomValueMapper): FeelEngineBuilder =
    copy(valueMapper = CompositeValueMapper(customValueMapper :: customValueMappers))

  /**
    * Sets the given [[ValueMapper]] for the engine. Overwrites the [[CustomValueMapper]]s that were set before.
    */
  def withValueMapper(valueMapper: ValueMapper): FeelEngineBuilder =
    copy(valueMapper = valueMapper)

  /**
    * Sets the given [[FeelEngineClock]] for the engine.
    */
  def withClock(clock: FeelEngineClock): FeelEngineBuilder =
    copy(clock = clock)

  /**
    * Enables/disables external FEEL functions for the engine.
    */
  def withEnabledExternalFunctions(enabled: Boolean): FeelEngineBuilder =
    copy(configuration = configuration.copy(externalFunctionsEnabled = enabled))


  /**
    * Creates a new engine with the given configuration.
    *
    * @return the API to access the engine.
    */
  def build(): FeelEngineApi = new FeelEngineApi(
    engine = new FeelEngine(
      functionProvider = functionProvider,
      valueMapper = valueMapper,
      configuration = configuration,
      clock = clock
    )
  )

}
