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

import org.camunda.feel.syntaxtree.Val
import scala.collection.JavaConverters._

abstract class JavaCustomValueMapper extends CustomValueMapper {

  /**
    * Transform the given object into a FEEL type - one of [[Val]] (e.g. [[Double]] to [[ValNumber]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param x                the object to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the FEEL representation of the object
    */
  def toValue(x: Any, innerValueMapper: java.util.function.Function[Any, Val])
    : java.util.Optional[Val]

  override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = {
    toValue(x, innerValue => innerValueMapper.apply(innerValue)) match {
      case v if (v.isPresent) => Some(v.get)
      case _                  => None
    }
  }

  /**
    * Transform the given FEEL type into a base Scala/Java object (e.g. [[ValNumber]] to [[Double]]).
    * If it can't be transformed then it returns [[None]] instead and the object is passed to the next mapper in the chain.
    *
    * @param value            the FEEL type to transform
    * @param innerValueMapper the mapper function to transform inner values of a collection type
    * @return the base object of the FEEL type
    */
  def unpackValue(value: Val,
                  innerValueMapper: java.util.function.Function[Val, Any])
    : java.util.Optional[Any]

  override def unpackVal(value: Val,
                         innerValueMapper: Val => Any): Option[Any] = {
    unpackValue(value, innerValue => innerValueMapper.apply(innerValue)) match {
      case x if (x.isPresent) => Some(x.get)
      case _                  => None
    }
  }

  /**
    * The priority of this mapper in the chain. The mappers are invoked in order of their priority,
    * starting with the highest priority.
    */
  override val priority: Int = 1
}

object JavaCustomValueMapper {

  case class CompositeValueMapper(
      customMappers: java.util.List[CustomValueMapper])
      extends ValueMapper {
    var valueMappers: ValueMapper.CompositeValueMapper =
      ValueMapper.CompositeValueMapper(customMappers.asScala.toList);

    override def toVal(x: Any): Val = valueMappers.toVal(x)

    override def unpackVal(value: Val): Any = valueMappers.unpackVal(value)
  }

}
