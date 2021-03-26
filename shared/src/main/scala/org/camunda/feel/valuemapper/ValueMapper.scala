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
package org.camunda.feel.valuemapper

import org.camunda.feel.impl.DefaultValueMapper
import org.camunda.feel.syntaxtree.Val

trait ValueMapper {

  def toVal(x: Any): Val

  def unpackVal(value: Val): Any

}

object ValueMapper {

  case class CompositeValueMapper(customMappers: List[CustomValueMapper])
      extends ValueMapper {

    val customMappersByPriority =
      (DefaultValueMapper.instance :: customMappers).distinct
        .sortBy(_.priority)(Ordering[Int].reverse)

    override def toVal(x: Any): Val = {
      for (customMapper <- customMappersByPriority) {
        customMapper.toVal(x, this.toVal) match {
          case Some(value) => return value
          case _           =>
        }
      }
      throw new IllegalArgumentException(
        s"no value mapper found for '$x' ('${x.getClass}')")
    }

    override def unpackVal(value: Val): Any = {
      for (customMapper <- customMappersByPriority) {
        customMapper.unpackVal(value, this.unpackVal) match {
          case Some(x) => return x
          case _       =>
        }
      }
      throw new IllegalArgumentException(s"no value mapper found for '$value'")
    }
  }

  val defaultValueMapper = CompositeValueMapper(
    List(DefaultValueMapper.instance)
  )

}
