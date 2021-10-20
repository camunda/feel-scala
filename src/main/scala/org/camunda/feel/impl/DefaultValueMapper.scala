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

import org.camunda.feel.{
  Date,
  DateTime,
  DayTimeDuration,
  LocalDateTime,
  LocalTime,
  Time,
  YearMonthDuration
}
import org.camunda.feel.context.Context
import org.camunda.feel.impl.interpreter.ObjectContext
import org.camunda.feel.syntaxtree.{
  Val,
  ValBoolean,
  ValContext,
  ValDate,
  ValDateTime,
  ValDayTimeDuration,
  ValError,
  ValFunction,
  ValList,
  ValLocalDateTime,
  ValLocalTime,
  ValNull,
  ValNumber,
  ValString,
  ValTime,
  ValYearMonthDuration,
  ZonedTime
}
import org.camunda.feel.valuemapper.CustomValueMapper

import java.time.{LocalDateTime, ZoneId}
import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}
import scala.math.BigDecimal

class DefaultValueMapper extends CustomValueMapper {

  def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = x match {

    case x: Val => Some(x)
    case null   => Some(ValNull)

    // scala types
    case x: Short                               => Some(ValNumber(x))
    case x: Int                                 => Some(ValNumber(x))
    case x: Long                                => Some(ValNumber(x))
    case x: Float if (x.isNaN || x.isInfinity)  => Some(ValNull)
    case x: Float                               => Some(ValNumber(x))
    case x: Double if (x.isNaN || x.isInfinity) => Some(ValNull)
    case x: Double                              => Some(ValNumber(x))
    case x: BigDecimal                          => Some(ValNumber(x))
    case x: BigInt                              => Some(ValNumber(BigDecimal(x)))
    case x: Boolean                             => Some(ValBoolean(x))
    case x: String                              => Some(ValString(x))
    case x: Date                                => Some(ValDate(x))
    case x: LocalTime                           => Some(ValLocalTime(x))
    case x: Time                                => Some(ValTime(x))
    case x: LocalDateTime                       => Some(ValLocalDateTime(x))
    case x: DateTime                            => Some(ValDateTime(x))
    case x: YearMonthDuration                   => Some(ValYearMonthDuration(x))
    case x: DayTimeDuration                     => Some(ValDayTimeDuration(x))
    case x: List[_]                             => Some(ValList(x map innerValueMapper))
    case x: Map[_, _] =>
      Some {
        val (functions, variables) = x
          .map {
            case (key, value) => key.toString -> innerValueMapper(value)
          }
          .partition { case (key, value) => value.isInstanceOf[ValFunction] }

        ValContext(
          Context.StaticContext(
            variables = variables,
            functions = functions.map {
              case (key, f) => key -> List(f.asInstanceOf[ValFunction])
            }
          ))
      }
    case Some(x)                 => Some(innerValueMapper(x))
    case None                    => Some(ValNull)
    case x if (isEnumeration(x)) => Some(ValString(x.toString))

    // extended java types
    case x: java.math.BigDecimal => Some(ValNumber(x))
    case x: java.math.BigInteger => Some(ValNumber(BigDecimal(x)))
    case x: java.util.Date =>
      Some(
        ValLocalDateTime(
          LocalDateTime.ofInstant(x.toInstant, ZoneId.systemDefault())
        )
      )
    case x: java.time.OffsetDateTime =>
      Some(
        ValDateTime(x.toZonedDateTime())
      )
    case x: java.time.OffsetTime =>
      Some(
        ValTime(ZonedTime.of(x))
      )
    case x: java.util.List[_] =>
      Some(
        ValList(x.asScala.toList map innerValueMapper)
      )
    case x: java.util.Map[_, _] =>
      Some(
        ValContext(Context.StaticContext(x.asScala.map {
          case (key, value) => key.toString -> innerValueMapper(value)
        }.toMap))
      )
    case x: java.lang.Enum[_] => Some(ValString(x.name))

    // other objects
    case x: Throwable => Some(ValError(x.getMessage))
    case x =>
      try {
        Some(
          ValContext(Context.CacheContext(ObjectContext(x)))
        )
      } catch {
        case _: Throwable => None
      }

  }

  // enumerations can't be used for pattern matching
  private def isEnumeration(x: Any) =
    "scala.Enumeration$Val" == x.getClass.getName

  def unpackVal(value: Val, innerValueMapper: Val => Any): Option[Any] =
    value match {
      case ValNull                        => Some(null)
      case ValBoolean(boolean)            => Some(boolean)
      case ValNumber(number)              => Some(number)
      case ValString(string)              => Some(string)
      case ValDate(date)                  => Some(date)
      case ValLocalTime(time)             => Some(time)
      case ValTime(time)                  => Some(time)
      case ValLocalDateTime(dateTime)     => Some(dateTime)
      case ValDateTime(dateTime)          => Some(dateTime)
      case ValYearMonthDuration(duration) => Some(duration)
      case ValDayTimeDuration(duration)   => Some(duration)
      case ValList(list)                  => Some(list map innerValueMapper)
      case ValContext(c: Context) =>
        Some(
          c.variableProvider.getVariables.map {
            case (key, value) =>
              value match {
                case packed: Val => key -> innerValueMapper(packed)
                case unpacked    => key -> unpacked
              }
          }.toMap
        )

      case f: ValFunction => Some(f)
      case e: ValError    => Some(e)

      case _ => None
    }

  // default value mapper should have the lowest priority
  override val priority: Int = 0
}

object DefaultValueMapper {

  val instance = new DefaultValueMapper

}
