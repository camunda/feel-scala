package org.camunda.feel.cli

import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      repl()
    } else {
      evalExpression(args.mkString(" "))
    }
  }

  private def repl(): Unit = {
    println("FEEL Expression Evaluator")
    println("Enter expressions (Ctrl+D to exit):")
    println()

    var line = StdIn.readLine("> ")
    while (line != null) {
      if (line.trim.nonEmpty) {
        FeelEvaluator.evaluate(line) match {
          case Right(result) => println(result)
          case Left(error)   => System.err.println(error)
        }
      }
      line = StdIn.readLine("> ")
    }
    println()
  }

  private def evalExpression(expression: String): Unit = {
    FeelEvaluator.evaluate(expression) match {
      case Right(result) => println(result)
      case Left(error)   =>
        System.err.println(error)
        sys.exit(1)
    }
  }
}
