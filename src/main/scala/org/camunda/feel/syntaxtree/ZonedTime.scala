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
package org.camunda.feel.syntaxtree

import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.TemporalAmount
import org.camunda.feel.{
  localTimeFormatter,
  timeFormatterWithOffsetAndOptionalPrefix
}

import java.time.{
  Duration,
  Instant,
  LocalDate,
  LocalTime,
  OffsetTime,
  ZoneId,
  ZoneOffset,
  ZonedDateTime
}
import scala.util.Try

case class ZonedTime(time: LocalTime,
                     offset: ZoneOffset,
                     zone: Option[ZoneId]) {

  import ZonedTime._

  val hasTimeZone = zone.isDefined

  val nanos = {
    val nod = time.toNanoOfDay
    val offsetNanos = offset.getTotalSeconds() * NANOS_PER_SECOND
    nod - offsetNanos
  }

  def plus(amountToAdd: TemporalAmount): ZonedTime = {
    val newTime = time.plus(amountToAdd)

    ZonedTime(newTime, offset, zone)
  }

  def minus(amountToAdd: TemporalAmount): ZonedTime = {
    val newTime = time.minus(amountToAdd)

    ZonedTime(newTime, offset, zone)
  }

  def compareTo(other: ZonedTime): Int = nanos compare other.nanos

  def isBefore(other: ZonedTime): Boolean = nanos < other.nanos

  def isAfter(other: ZonedTime): Boolean = nanos > other.nanos

  def getHour: Int = time.getHour

  def getMinute: Int = time.getMinute

  def getSecond: Int = time.getSecond

  def getOffsetInTotalSeconds: Int = offset.getTotalSeconds

  def getZoneId: Option[String] = zone.map(_.getId)

  def between(other: ZonedTime): Duration = {
    val diff = (nanos - other.nanos).abs

    Duration.ofNanos(diff)
  }

  def format: String = {
    val localTime = localTimeFormatter.format(time)

    if (hasTimeZone) {
      val zoneId = zone.get.getId

      s"$localTime@$zoneId"
    } else {
      localTime + offsetFormatter.format(offset)
    }
  }

  def withDate(date: LocalDate): ZonedDateTime = {
    val localDateTime = date.atTime(time)

    if (hasTimeZone) {
      localDateTime.atZone(zone.get)
    } else {
      localDateTime.atOffset(offset).toZonedDateTime
    }
  }

  def toOffsetTime: OffsetTime = time.atOffset(offset)

  def toLocalTime: LocalTime = time

}

object ZonedTime {

  val NANOS_PER_SECOND = 1000000000L

  val offsetFormatter = new DateTimeFormatterBuilder()
    .appendOffsetId()
    .toFormatter()

  def parse(time: String): ZonedTime = {
    val temporal = timeFormatterWithOffsetAndOptionalPrefix.parse(time)

    val localTime = LocalTime.from(temporal)

    val zoneId = ZoneId.from(temporal)
    val offset: ZoneOffset = Try(ZoneOffset.from(temporal))
      .getOrElse(zoneId.getRules.getStandardOffset(Instant.now))

    val zone = if (offset.equals(zoneId)) {
      None
    } else {
      Some(zoneId)
    }

    ZonedTime(localTime, offset, zone)
  }

  def of(time: LocalTime, offset: ZoneOffset): ZonedTime = {
    ZonedTime(time, offset, None)
  }

  def of(time: LocalTime, zoneId: ZoneId): ZonedTime = {
    val offset = zoneId.getRules.getStandardOffset(Instant.now)
    ZonedTime(time, offset, Some(zoneId))
  }

  def of(offsetTime: OffsetTime): ZonedTime = {
    val localTime = offsetTime.toLocalTime()
    val offset = offsetTime.getOffset

    ZonedTime(localTime, offset, None)
  }

  def of(dateTime: ZonedDateTime): ZonedTime = {
    val localTime = dateTime.toLocalTime()
    val offset = dateTime.getOffset
    val zone = {
      if (dateTime.getZone.equals(offset)) {
        None
      } else {
        Some(dateTime.getZone)
      }
    }

    ZonedTime(localTime, offset, zone)
  }

  def between(x: ZonedTime, y: ZonedTime): Duration = x.between(y)

}
