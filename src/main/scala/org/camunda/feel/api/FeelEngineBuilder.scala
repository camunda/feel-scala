/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.api

import org.camunda.feel.{FeelEngine, FeelEngineClock}
import org.camunda.feel.FeelEngine.{Configuration, defaultFunctionProvider}
import org.camunda.feel.context.FunctionProvider
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper

/** Builds a new instance of the FEEL engine. Use the setters to customize the engine.
  */
case class FeelEngineBuilder() {

  private var functionProvider: FunctionProvider          = defaultFunctionProvider
  private var valueMapper: ValueMapper                    = FeelEngine.defaultValueMapper
  private var customValueMappers: List[CustomValueMapper] = List.empty
  private var clock: FeelEngineClock                      = FeelEngine.defaultClock
  private var configuration: Configuration                = FeelEngine.defaultConfiguration

  /** Sets the given [[FunctionProvider]] for the engine.
    */
  def withFunctionProvider(functionProvider: FunctionProvider): FeelEngineBuilder = {
    this.functionProvider = functionProvider
    this
  }

  /** Sets the given [[CustomValueMapper]] for the engine. Can be called multiple times to chain the
    * value mappers.
    */
  def withCustomValueMapper(customValueMapper: CustomValueMapper): FeelEngineBuilder = {
    this.valueMapper = CompositeValueMapper(customValueMapper :: customValueMappers)
    this
  }

  /** Sets the given [[CustomValueMapper]] for the engine. Can be called multiple times to chain the
    * value mappers.
    */
  def withCustomValueMappers(customValueMappers: List[CustomValueMapper]): FeelEngineBuilder = {
    this.customValueMappers = customValueMappers
    this
  }

  /** Sets the given [[ValueMapper]] for the engine. Overwrites the [[CustomValueMapper]]s that were
    * set before.
    */
  def withValueMapper(valueMapper: ValueMapper): FeelEngineBuilder = {
    this.valueMapper = valueMapper
    this
  }

  /** Sets the given [[FeelEngineClock]] for the engine.
    */
  def withClock(clock: FeelEngineClock): FeelEngineBuilder = {
    this.clock = clock
    this
  }

  /** Enables/disables external FEEL functions for the engine.
    */
  def withEnabledExternalFunctions(enabled: Boolean): FeelEngineBuilder = {
    configuration = configuration.copy(externalFunctionsEnabled = enabled)
    this
  }

  /** Creates a new engine with the given configuration.
    *
    * @return
    *   the API to access the engine.
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
