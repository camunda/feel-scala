package org.camunda.feel.cli

import caseapp._
import caseapp.core.app.CaseApp
import caseapp.core.help.Help

import java.io.File

@AppName("FEEL Expression Evaluator CLI")
@AppVersion("1.0.0")
@ProgName("feel")
case class CliOptions(
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

object CliOptions {
  implicit val parser: Parser[CliOptions] = Parser.derive
  implicit val help: Help[CliOptions]     = Help.derive
}
