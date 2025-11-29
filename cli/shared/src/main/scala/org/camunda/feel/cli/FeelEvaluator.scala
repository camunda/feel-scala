package org.camunda.feel.cli

import org.camunda.feel.api.{FeelEngineApi, FeelEngineBuilder}
import scala.io.Source
import java.io.File

/** Shared FEEL expression evaluation logic */
object FeelEvaluator {

  lazy val engine: FeelEngineApi = FeelEngineBuilder.create().build()

  /** Convert ujson.Value to Map[String, Any] */
  private def jsonToMap(value: ujson.Value): Map[String, Any] = {
    value match {
      case ujson.Obj(obj) => obj.map { case (k, v) => k -> jsonToAny(v) }.toMap
      case _              => throw new IllegalArgumentException("Context must be a JSON object")
    }
  }

  /** Convert ujson.Value to Any */
  private def jsonToAny(value: ujson.Value): Any = {
    value match {
      case ujson.Str(s)   => s
      case ujson.Num(n)   => if (n.isValidInt) n.toInt else n.toDouble
      case ujson.Bool(b)  => b
      case ujson.Null     => null
      case ujson.Arr(arr) => arr.map(jsonToAny)
      case ujson.Obj(obj) => obj.map { case (k, v) => k -> jsonToAny(v) }.toMap
    }
  }

  /** Evaluate an expression and return the result or error message */
  def evaluate(expression: String, context: Option[String] = None): Either[String, String] = {
    val result = context match {
      case Some(ctx) =>
        try {
          val parsedContext = ujson.read(ctx)
          val variables     = jsonToMap(parsedContext)
          engine.evaluateExpression(expression, variables)
        } catch {
          case e: Exception =>
            return Left(s"Invalid JSON context: ${e.getMessage}")
        }
      case None      =>
        engine.evaluateExpression(expression)
    }

    if (result.isSuccess) {
      Right(Option(result.result).fold("null")(_.toString))
    } else {
      Left(s"Error: ${result.failure}")
    }
  }

  /** Evaluate expressions from a file, one per line */
  def evaluateFile(
      fileStr: String,
      context: Option[String] = None
  ): Either[String, List[(String, Either[String, String])]] = {
    var file = new File(fileStr)
    if (!file.exists()) {
      return Left(s"File not found: ${file.getPath}")
    }

    if (!file.isFile()) {
      return Left(s"Path is not a file: ${file.getPath}")
    }

    try {
      val lines   = Source.fromFile(file).getLines().toList
      val results = lines.map { line =>
        val trimmed = line.trim
        if (trimmed.isEmpty || trimmed.startsWith("#")) {
          trimmed -> Right("")
        } else {
          trimmed -> evaluate(trimmed, context)
        }
      }
      Right(results)
    } catch {
      case e: Exception => Left(s"Error reading file: ${e.getMessage}")
    }
  }

  /** Format evaluation results for output */
  def formatResult(expression: String, result: Either[String, String], verbose: Boolean): String = {
    result match {
      case Right(value) =>
        if (verbose) s"$expression => $value"
        else value
      case Left(error)  =>
        if (verbose) s"$expression => $error"
        else error
    }
  }
}
