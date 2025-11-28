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
package org.camunda.feel.impl.interpreter

import org.camunda.feel.api.EvaluationFailureType
import org.camunda.feel.syntaxtree.Val
import org.camunda.feel.valuemapper.ValueMapper

object JavaFunctionInvoker {
  def invokeJavaFunction(
                          className: String,
                          methodName: String,
                          arguments: List[String],
                          paramValues: List[Val],
                          valueMapper: ValueMapper
                        ): Val = {
    val clazz = JavaClassMapper.loadClass(className)

    val argTypes = arguments map JavaClassMapper.loadClass

    val method = clazz.getDeclaredMethod(methodName, argTypes: _*)

    val argJavaObjects = paramValues zip argTypes map { case (obj, clazz) =>
      JavaClassMapper.asJavaObject(obj, clazz)
    }

    val result = method.invoke(null, argJavaObjects: _*)

    valueMapper.toVal(result)
  }

}
