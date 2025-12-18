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
import org.camunda.feel.impl.JavaValueMapper
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

/** Builds a new instance of the FEEL engine. Use the setters to customize the engine.
  */
case class FeelEngineBuilder private (
    functionProvider: FunctionProvider = defaultFunctionProvider,
    valueMapper: ValueMapper = FeelEngine.defaultValueMapper,
    customValueMappers: List[CustomValueMapper] = List.empty,
    clock: FeelEngineClock = FeelEngine.defaultClock,
    configuration: Configuration = FeelEngine.defaultConfiguration
) {

  /** Sets the given [[FunctionProvider]] for the engine.
    */
  def withFunctionProvider(functionProvider: FunctionProvider): FeelEngineBuilder =
    copy(functionProvider = functionProvider)

  /** Sets the given [[CustomValueMapper]] for the engine. Can be called multiple times to chain the
    * value mappers.
    */
  def withCustomValueMapper(customValueMapper: CustomValueMapper): FeelEngineBuilder =
    copy(valueMapper = CompositeValueMapper(customValueMapper :: customValueMappers))

  /** Sets the given [[ValueMapper]] for the engine. Overwrites the [[CustomValueMapper]]s that were
    * set before.
    */
  def withValueMapper(valueMapper: ValueMapper): FeelEngineBuilder =
    copy(valueMapper = valueMapper)

  /** Sets the given [[FeelEngineClock]] for the engine.
    */
  def withClock(clock: FeelEngineClock): FeelEngineBuilder =
    copy(clock = clock)

  /** Enables/disables external FEEL functions for the engine.
    */
  def withEnabledExternalFunctions(enabled: Boolean): FeelEngineBuilder =
    copy(configuration = configuration.copy(externalFunctionsEnabled = enabled))

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

  /** Creates a new engine that applies the given default evaluation timeout.
    *
    * The evaluation is run on a separate thread and interrupted on timeout.
    */
  def buildWithEvaluationTimeout(timeout: FiniteDuration): FeelEngineApi =
    new TimedFeelEngineApi(
      engine = new FeelEngine(
        functionProvider = functionProvider,
        valueMapper = valueMapper,
        configuration = configuration,
        clock = clock
      ),
      timeout = timeout
    )

  /** Java-friendly overload. */
  def buildWithEvaluationTimeout(timeout: java.time.Duration): FeelEngineApi =
    buildWithEvaluationTimeout(timeout.toMillis.millis)

}

object FeelEngineBuilder {

  /** Creates a new builder for the FEEL engine.
    *
    * @return
    *   a new builder
    */
  def create(): FeelEngineBuilder = FeelEngineBuilder()

  /** Creates a new preconfigured builder for the FEEL engine. Use it if the engine is called from
    * Java code.
    *
    * @return
    *   a new builder
    */
  def forJava(): FeelEngineBuilder = FeelEngineBuilder().withCustomValueMapper(new JavaValueMapper)
}
