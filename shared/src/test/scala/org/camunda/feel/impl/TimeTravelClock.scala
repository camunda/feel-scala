package org.camunda.feel.impl

import java.time.ZonedDateTime

import org.camunda.feel.FeelEngineClock

class TimeTravelClock extends FeelEngineClock {

  private val systemClock = FeelEngineClock.SystemClock

  private var provider: () => ZonedDateTime = () => systemClock.getCurrentTime

  def currentTime(currentTime: ZonedDateTime) {
    provider = () => currentTime
  }

  def reset(): Unit = {
    provider = () => systemClock.getCurrentTime
  }

  override def getCurrentTime: ZonedDateTime = provider()
}
