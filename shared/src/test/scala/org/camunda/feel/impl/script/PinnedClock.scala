package org.camunda.feel.impl.script

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.camunda.feel.FeelEngineClock

class PinnedClock extends FeelEngineClock {

  override def getCurrentTime: ZonedDateTime = PinnedClock.currentTime
}

object PinnedClock {

  var currentTime = ZonedDateTime.now()

}
