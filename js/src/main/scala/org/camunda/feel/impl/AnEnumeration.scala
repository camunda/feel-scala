package org.camunda.feel.impl

import scala.scalajs.js

object AnEnumeration {
  def unapply(a: Any): Option[Any] =
    if (js.Object
          .properties(a.asInstanceOf[js.Any])
          .contains("s_Enumeration$Val__f_name")) Some(a)
    else None
}
