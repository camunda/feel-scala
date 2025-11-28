package org.camunda.feel.cli

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("readline", JSImport.Namespace)
object Readline extends js.Object {
  def createInterface(options: js.Dynamic): ReadlineInterface = js.native
}

@js.native
trait ReadlineInterface extends js.Object {
  def question(query: String, callback: js.Function1[String, Unit]): Unit = js.native
  def close(): Unit = js.native
  def on(event: String, callback: js.Function0[Unit]): Unit = js.native
}

object Main {

  def main(args: Array[String]): Unit = {
    // In Node.js, process.argv contains: [node, script, ...args]
    val nodeArgs = if (js.typeOf(global.process) != "undefined") {
      global.process.argv.asInstanceOf[js.Array[String]].toArray.drop(2)
    } else {
      args
    }

    val effectiveArgs = if (args.nonEmpty) args else nodeArgs

    if (effectiveArgs.isEmpty) {
      repl()
    } else {
      evalExpression(effectiveArgs.mkString(" "))
    }
  }

  private def repl(): Unit = {
    println("FEEL Expression Evaluator")
    println("Enter expressions (Ctrl+D to exit):")
    println()

    val rl = Readline.createInterface(js.Dynamic.literal(
      input = global.process.stdin,
      output = global.process.stdout
    ))

    def prompt(): Unit = {
      rl.question("> ", { (line: String) =>
        if (line.trim.nonEmpty) {
          FeelEvaluator.evaluate(line) match {
            case Right(result) => println(result)
            case Left(error)   => System.err.println(error)
          }
        }
        prompt()
      })
    }

    rl.on("close", { () =>
      println()
    })

    prompt()
  }

  private def evalExpression(expression: String): Unit = {
    FeelEvaluator.evaluate(expression) match {
      case Right(result) => println(result)
      case Left(error)   =>
        System.err.println(error)
    }
  }
}
