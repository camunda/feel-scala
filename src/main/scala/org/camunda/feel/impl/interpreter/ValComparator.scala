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

import org.camunda.feel.context.Context
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.ValueMapper

class ValComparator(private val valueMapper: ValueMapper) {

  def equals(x: Val, y: Val): Boolean = compare(x, y) match {
    case ValBoolean(isEqual) => isEqual
    case _                   => false
  }

  def hashCode(x: Val): Int = x match {
    case ValNull                 => 0
    case ValNumber(n)            => n.hashCode()
    case ValBoolean(b)           => b.hashCode()
    case ValString(s)            => s.hashCode()
    case ValDate(d)              => d.hashCode()
    case ValLocalTime(t)         => t.hashCode()
    case ValTime(t)              => t.hashCode()
    case ValLocalDateTime(dt)    => dt.hashCode()
    case ValDateTime(dt)         => dt.hashCode()
    case ValYearMonthDuration(d) => d.hashCode()
    case ValDayTimeDuration(d)   => d.hashCode()
    case ValList(items)          => items.map(hashCode).hashCode()
    case ValContext(ctx)         => hashCodeContext(ctx)
    case _: ValError             => 0
    case _: ValRange             => x.hashCode()
    case _: ValFunction          => x.hashCode()
    case _: ValFatalError        => x.hashCode()
  }

  private def hashCodeContext(ctx: Context): Int = {
    ctx.variableProvider.getVariables.iterator
      .map { case (k, v) =>
        k.hashCode() ^ hashCode(valueMapper.toVal(v))
      }
      .foldLeft(0)(_ ^ _)
  }

  def compare(x: Val, y: Val): Val = (x, y) match {
    // both values are null
    case (ValNull, _)                                       => ValBoolean(ValNull == y.toOption.getOrElse(ValNull))
    case (_, ValNull)                                       => ValBoolean(x.toOption.getOrElse(ValNull) == ValNull)
    // compare values of the same type
    case (ValNumber(x), ValNumber(y))                       => ValBoolean(x == y)
    case (ValBoolean(x), ValBoolean(y))                     => ValBoolean(x == y)
    case (ValString(x), ValString(y))                       => ValBoolean(x == y)
    case (ValDate(x), ValDate(y))                           => ValBoolean(x == y)
    case (ValLocalTime(x), ValLocalTime(y))                 => ValBoolean(x == y)
    case (ValTime(x), ValTime(y))                           => ValBoolean(x == y)
    case (ValLocalDateTime(x), ValLocalDateTime(y))         => ValBoolean(x == y)
    case (ValDateTime(x), ValDateTime(y))                   => ValBoolean(x == y)
    case (ValYearMonthDuration(x), ValYearMonthDuration(y)) => ValBoolean(x == y)
    case (ValDayTimeDuration(x), ValDayTimeDuration(y))     => ValBoolean(x == y)
    case (ValList(x), ValList(y))                           => compare(x, y)
    case (ValContext(x), ValContext(y))                     => compare(x, y)
    // values have a different type
    case _                                                  => ValError(s"Can't compare '$x' with '$y'")
  }

  private def compare(x: List[Val], y: List[Val]): ValBoolean = {
    ValBoolean(
      x.size == y.size && x.zip(y).forall { case (itemX, itemY) => equals(itemX, itemY) }
    )
  }

  private def compare(x: Context, y: Context): ValBoolean = {
    val xVars = x.variableProvider.getVariables
    val yVars = y.variableProvider.getVariables

    ValBoolean(xVars.keys == yVars.keys && xVars.keys.forall { key =>
      val xVal = valueMapper.toVal(xVars(key))
      val yVal = valueMapper.toVal(yVars(key))

      equals(xVal, yVal)
    })
  }

}
