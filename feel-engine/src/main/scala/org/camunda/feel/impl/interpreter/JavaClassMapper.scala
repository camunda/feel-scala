package org.camunda.feel.impl.interpreter

/**
  * @author Philipp
  */
object JavaClassMapper {

  val classLoader = getClass.getClassLoader

  val stringClass = classOf[java.lang.String]

  def loadClass(className: String): Class[_] = className match {
    case "int"     => java.lang.Integer.TYPE
    case "long"    => java.lang.Long.TYPE
    case "float"   => java.lang.Float.TYPE
    case "double"  => java.lang.Double.TYPE
    case "boolean" => java.lang.Boolean.TYPE
    case "String"  => stringClass
    case _         => classLoader.loadClass(className)
  }

  def asJavaObject(value: Val, clazz: Class[_]): java.lang.Object =
    (value, clazz) match {
      case (ValNull, _) => null
      case (ValBoolean(b), java.lang.Boolean.TYPE) =>
        java.lang.Boolean.valueOf(b)
      case (ValString(s), stringClass) => java.lang.String.valueOf(s)
      case (ValNumber(n), java.lang.Integer.TYPE) =>
        java.lang.Integer.valueOf(n.intValue)
      case (ValNumber(n), java.lang.Long.TYPE) =>
        java.lang.Long.valueOf(n.longValue)
      case (ValNumber(n), java.lang.Float.TYPE) =>
        java.lang.Float.valueOf(n.floatValue)
      case (ValNumber(n), java.lang.Double.TYPE) =>
        java.lang.Double.valueOf(n.doubleValue)
      case _ =>
        throw new IllegalArgumentException(
          s"can not cast value '$value' to class '$clazz'")
    }

}
