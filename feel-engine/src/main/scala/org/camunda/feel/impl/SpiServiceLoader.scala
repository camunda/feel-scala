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
package org.camunda.feel.impl

import java.util.ServiceLoader

import org.camunda.feel.context.{CustomFunctionProvider, FunctionProvider}
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}

import scala.reflect.{ClassTag, classTag}
import scala.collection.JavaConverters._

object SpiServiceLoader {

  def loadValueMapper: ValueMapper = {
    val customValueMappers = loadServiceProvider[CustomValueMapper]()
    ValueMapper.CompositeValueMapper(customValueMappers)
  }

  def loadFunctionProvider: FunctionProvider =
    loadServiceProvider[CustomFunctionProvider]() match {
      case Nil      => FunctionProvider.EmptyFunctionProvider
      case p :: Nil => p
      case ps       => FunctionProvider.CompositeFunctionProvider(ps)
    }

  private def loadServiceProvider[T: ClassTag](): List[T] =
    try {
      val loader =
        ServiceLoader.load(classTag[T].runtimeClass.asInstanceOf[Class[T]])
      loader.iterator.asScala.toList
    } catch {
      case t: Throwable =>
        System.err.println(
          s"Failed to load service provider: ${classTag[T].runtimeClass.getSimpleName}")
        t.printStackTrace()
        throw t
    }

}
