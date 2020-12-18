package org.camunda.feel.impl.interpreter

import org.camunda.feel.context.{Context, FunctionProvider, VariableProvider}
import org.camunda.feel.syntaxtree.ValFunction

import scala.scalajs.js

/** A context that wraps the fields and methods of a given JVM object
  *
  * @param obj the JVM object to be wrapped
  */
case class ObjectContext(obj: Any) extends Context {
  def theObj = obj.asInstanceOf[js.Object with js.Dynamic]

  lazy val entries = js.Object.getOwnPropertyNames(theObj) ++ js.Object
    .getOwnPropertyNames(js.Object.getPrototypeOf(theObj))
  lazy val objName = theObj.$classData.arrayEncodedName
    .asInstanceOf[String]
    .replaceAll("\\.", "_")
    .replace("$", "\\$")
    .replaceAll(";$", "")
  val GetOrIs = "^(get|is)([A-Z])(\\w*)".r
  def normalizeFieldName(name: String) = name match {
    case GetOrIs(_, firstLetter, rest) => s"${firstLetter.toLowerCase()}$rest"
    case s                             => s
  }
  override val variableProvider = new VariableProvider {
    override def getVariable(name: String): Option[Any] = {
      val fieldMatcher = s"^${objName}__(.)_${name}$$".r
      val fields = entries.find(fieldMatcher.matches)
      js.special.debugger()
      fields.map(f => theObj.selectDynamic(f))
    }

    override def keys: Iterable[String] =
      js.Object.properties(obj.asInstanceOf[js.Any])
  }

  def functionsWithName(name: String): js.Array[String] = {
    val methodMatcher = s"^${name}__(.)(__.)?$$".r
    entries.filter(methodMatcher.matches)
  }

  override val functionProvider = new FunctionProvider {
    override def getFunctions(name: String): List[ValFunction] = {
      val obj = ObjectContext.this.obj.asInstanceOf[js.Object]
      val res = functionsWithName(name)
        .map(f => ValFunction(List(), _ => theObj.applyDynamic(f)()))
        .toList
      js.special.debugger()
      res
    }

    override def functionNames: Iterable[String] =
      js.Object.properties(obj.asInstanceOf[js.Any])
  }

}
