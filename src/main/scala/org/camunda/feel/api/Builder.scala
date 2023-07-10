package org.camunda.feel.api

import org.camunda.feel.{FeelEngine, FeelEngineClock}
import org.camunda.feel.FeelEngine.{Configuration, defaultFunctionProvider}
import org.camunda.feel.context.FunctionProvider
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper

class Builder() {

  private var functionProvider_ : FunctionProvider = defaultFunctionProvider
  private var valueMapper_ : ValueMapper = FeelEngine.defaultValueMapper
  private var customValueMappers_ : List[CustomValueMapper] = List.empty
  private var clock_ : FeelEngineClock = FeelEngine.defaultClock
  private var configuration_ : Configuration = FeelEngine.defaultConfiguration

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

  def clock(clock: FeelEngineClock): Builder = {
    clock_ = clock
    this
  }

  def enableExternalFunctions(enable: Boolean): Builder = {
    configuration_ = configuration_.copy(externalFunctionsEnabled = enable)
    this
  }

  def build: FeelEngineApi = new FeelEngineApi(
    engine = new FeelEngine(
      functionProvider = functionProvider_,
      valueMapper = valueMapper_,
      configuration = configuration_,
      clock = clock_
    )
  )

}
