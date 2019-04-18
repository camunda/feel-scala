package org.camunda.feel.spi

import java.util.{Collections, Optional}
import org.camunda.feel.interpreter._
import scala.collection.JavaConverters._

/**
  * Provides one or more functions which can be used in an expression.
  */
abstract class JavaFunctionProvider extends CustomFunctionProvider {

  /**
    * Returns the function for the given name.
    *
    * @param functionName the name of the function
    * @return the function or [[Optional.empty()]], if no function is provided for this name
    */
  def resolveFunction(functionName: String): Optional[JavaFunction]

  /**
    * Returns a list of functions for the given name. There can be multiple functions with different parameters.
    *
    * @param functionName the name of the function
    * @return a list of functions or an empty list, if no function is provided for this name
    */
  def resolveFunctions(functionName: String): java.util.List[JavaFunction] = {
    val function = resolveFunction(functionName)

    if (function.isPresent) {
      Collections.singletonList(function.get)
    } else {
      Collections.emptyList()
    }
  }

  override def getFunctions(name: String): List[ValFunction] = {
    resolveFunctions(name).asScala.map(f => asFunction(f)).toList
  }

  override def getFunction(name: String): Option[ValFunction] = {
    getFunctions(name).headOption
  }

  private def asFunction(function: JavaFunction): ValFunction = {

    val paramList: List[String] = function.getParams().asScala.toList

    val f: (List[Val] => Val) = (args: List[Val]) => {

      val argList: java.util.List[Val] = args.asJava

      function.getFunction().apply(argList)
    }

    ValFunction(paramList, f, function.hasVarArgs)
  }

}
