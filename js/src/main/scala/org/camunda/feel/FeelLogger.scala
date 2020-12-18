package org.camunda.feel

object FeelLogger {
  val logger = new FeelLogger
}

class FeelLogger {
  def error(message: => String) = println(message)
  def warn(message: => String) = println(message)
  def info(message: => String) = println(message)
  def debug(message: => String) = println(message)
  def trace(message: => String) = println(message)
}
