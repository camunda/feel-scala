package org.camunda.feel.cli

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

object Main {

  def main(args: Array[String]): Unit = {
    // In Node.js, process.argv contains: [node, script, ...args]
    // Scala.js should pass args correctly, but let's also check process.argv
    val nodeArgs = if (js.typeOf(global.process) != "undefined") {
      global.process.argv.asInstanceOf[js.Array[String]].toArray.drop(2)
    } else {
      args
    }
    
    val effectiveArgs = if (args.nonEmpty) args else nodeArgs

    if (effectiveArgs.isEmpty) {
      System.err.println("Usage: node main.js <expression>")
      System.err.println("Example: node main.js \"1 + 2\"")
    } else {
      evalExpression(effectiveArgs.mkString(" "))
    }
  }

  private def evalExpression(expression: String): Unit = {
    FeelEvaluator.evaluate(expression) match {
      case Right(result) => println(result)
      case Left(error)   =>
        System.err.println(error)
    }
  }
}
