package org.camunda.feel.cli

import caseapp._

// Define options outside main object
case class MyOptions(
  @HelpMessage("Evaluate a single FEEL expression")
  @Name("e")
  @Name("expression")
  expression: Option[String] = None,
  
  @HelpMessage("Evaluate expressions from a file")
  @Name("f")
  @Name("file")
  file: Option[String] = None,
  
  @HelpMessage("JSON context for variable evaluation")
  @Name("c")
  @Name("context")
  context: Option[String] = None,
  
  @HelpMessage("Enable verbose output")
  @Name("v")
  @Name("verbose")
  verbose: Boolean = false
)

object Main extends CaseApp[MyOptions] {

  override def run(options: MyOptions, remainingArgs: RemainingArgs): Unit = {

// Validate that either expression or file is provided
    if (options.expression.isEmpty && options.file.isEmpty) {
      Console.err.println("Error: Either --expression or --file must be specified")
      sys.exit(1)
    }

    // Execute expression if provided
    options.expression.foreach { expr =>
      FeelEvaluator.evaluate(expr, options.context) match {
        case Right(result) =>
          println(FeelEvaluator.formatResult(expr, Right(result), options.verbose))
        case Left(error)   =>
          System.err.println(FeelEvaluator.formatResult(expr, Left(error), options.verbose))
          sys.exit(1)
      }
    }

    // Execute file if provided
    options.file.foreach { file =>
      FeelEvaluator.evaluateFile(file, options.context) match {
        case Right(results) =>
          results.foreach { case (expr, result) =>
            if (expr.trim.nonEmpty && !expr.startsWith("#")) {
              val output = FeelEvaluator.formatResult(expr, result, options.verbose)
              if (result.isLeft) {
                System.err.println(output)
              } else {
                println(output)
              }
            }
          }
        case Left(error)    =>
          System.err.println(error)
          sys.exit(1)
      }
    }
  }
}

