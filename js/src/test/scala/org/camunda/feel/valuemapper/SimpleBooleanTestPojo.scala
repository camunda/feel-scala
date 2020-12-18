package org.camunda.feel.valuemapper

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SimpleBooleanTestPojo")
class SimpleBooleanTestPojo extends js.Object {

  protected var disabled: Boolean = _

  protected var enabled: java.lang.Boolean = _

  protected var foo: String = _

  def isFoo(): String = foo

  def setFoo(foo: String): Unit = {
    this.foo = foo
  }

  def isEnabled(): java.lang.Boolean = enabled

  def setEnabled(enabled: java.lang.Boolean): Unit = {
    this.enabled = enabled
  }

  def getDisabled(): Boolean = disabled

  def setDisabled(disabled: Boolean): Unit = {
    this.disabled = disabled
  }

}
