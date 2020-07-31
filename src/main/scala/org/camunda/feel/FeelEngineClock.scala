package org.camunda.feel

import java.time.ZonedDateTime

/**
  * The clock that is used by the engine to access the current time.
  */
trait FeelEngineClock {

  /**
    * @return the current time of the clock
    */
  def getCurrentTime: ZonedDateTime

}

object FeelEngineClock {

  /**
    * Access the current time from the system clock.
    */
  object SystemClock extends FeelEngineClock {
    override def getCurrentTime: ZonedDateTime = ZonedDateTime.now()

    override def toString: String = "SystemClock"
  }

}
