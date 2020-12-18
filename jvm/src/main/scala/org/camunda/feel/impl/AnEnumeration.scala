package org.camunda.feel.impl

object AnEnumeration {
  def unapply(x: Any): Option[Any] =
    if ("scala.Enumeration$Val" == x.getClass.getName) Some(x) else None
}
