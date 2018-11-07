package org.camunda.feel.spi

import org.camunda.feel.interpreter._
import scala.collection.JavaConverters
import scala.collection.JavaConverters._

abstract class JavaFunctionProvider extends CustomFunctionProvider {

  def resolveFunctions(functionName: String): java.util.List[JavaFunction]

  override def getFunction(name: String): List[ValFunction] = {
    resolveFunctions(name).asScala.map(f => asFunction(f)).toList
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
