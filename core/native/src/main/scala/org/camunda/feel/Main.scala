package org.camunda.feel

import org.camunda.feel.api.FeelEngineBuilder

object Main {
  def main(args: Array[String]): Unit = {
    val engine = FeelEngineBuilder.create().build()

    if (args.isEmpty) {
      // Interactive mode - read from stdin
      println("FEEL Expression Evaluator")
      println("Enter expressions (Ctrl+D to exit):")
      println()

      var line = scala.io.StdIn.readLine("> ")
      while (line != null) {
        if (line.trim.nonEmpty) {
          val result = engine.evaluateExpression(line)
          if (result.isSuccess) {
            println(result.result)
          } else {
            System.err.println(s"Error: ${result.failure}")
          }
        }
        line = scala.io.StdIn.readLine("> ")
      }
      println()
    } else {
      // Evaluate expression from command line argument
      val expression = args.mkString(" ")
      val result = engine.evaluateExpression(expression)
      if (result.isSuccess) {
        println(result.result)
      } else {
        System.err.println(s"Error: ${result.failure}")
        sys.exit(1)
      }
    }
  }
}
