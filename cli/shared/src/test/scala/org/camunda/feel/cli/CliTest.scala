package org.camunda.feel.cli

import java.nio.file.Paths
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CliTest extends AnyFunSuite with Matchers {

  // Use filesystem path instead of classloader resources (not available in Scala Native)
  private val testResourcesPath =
    Paths.get("cli", "shared", "src", "test", "resources").toAbsolutePath.toString

  test("FeelEvaluator should evaluate single expression") {
    val result = FeelEvaluator.evaluate("1 + 2")
    result shouldBe Right("3")
  }

  test("FeelEvaluator should evaluate expression with context") {
    val result = FeelEvaluator.evaluate("x + y", Some("{\"x\": 5, \"y\": 3}"))
    result shouldBe Right("8")
  }

  test("FeelEvaluator should evaluate expressions from file") {
    val filePath = Paths.get(testResourcesPath, "test-expressions.feel").toString
    val result   = FeelEvaluator.evaluateFile(filePath)
    result match {
      case Right(results) =>
        val filteredResults = results.filter { case (expr, _) =>
          expr.trim.nonEmpty && !expr.startsWith("#")
        }
        filteredResults(0)._2 shouldBe Right("2")           // 1 + 1
        filteredResults(1)._2 shouldBe Right("6")           // 2 * 3
        filteredResults(2)._2 shouldBe Right("true")        // 5 > 3
        filteredResults(3)._2 shouldBe Right("hello world") // "hello" + " " + "world"
        filteredResults(4)._2 shouldBe Right("null")        // x + y (no context)
        filteredResults(5)._2 shouldBe Right("null") // price * quantity (no context)
      case Left(error)    => fail(s"File evaluation failed: $error")
    }
  }

  test("FeelEvaluator should evaluate expressions from file with context") {
    val filePath = Paths.get(testResourcesPath, "test-expressions.feel").toString
    val context  = "{\"x\": 10, \"y\": 20, \"price\": 5.5, \"quantity\": 3}"
    val result   = FeelEvaluator.evaluateFile(filePath, Some(context))
    result match {
      case Right(results) =>
        val filteredResults = results.filter { case (expr, _) =>
          expr.trim.nonEmpty && !expr.startsWith("#")
        }
        filteredResults(0)._2 shouldBe Right("2")           // 1 + 1
        filteredResults(1)._2 shouldBe Right("6")           // 2 * 3
        filteredResults(2)._2 shouldBe Right("true")        // 5 > 3
        filteredResults(3)._2 shouldBe Right("hello world") // "hello" + " " + "world"
        filteredResults(4)._2 shouldBe Right("30")          // x + y
        filteredResults(5)._2 shouldBe Right("16.5") // price * quantity
      case Left(error)    => fail(s"File evaluation failed: $error")
    }
  }

  test("FeelEvaluator should format result correctly") {
    val expr   = "1 + 2"
    val result = Right("3")

    FeelEvaluator.formatResult(expr, result, verbose = false) shouldBe "3"
    FeelEvaluator.formatResult(expr, result, verbose = true) shouldBe "1 + 2 => 3"
  }

  test("FeelEvaluator should handle invalid context") {
    val result = FeelEvaluator.evaluate("1 + 2", Some("{invalid json}"))
    result shouldBe Left("Invalid JSON context: expected json value or } got \"i\" at index 1")
  }

  test("FeelEvaluator should handle non-existent file") {
    val result = FeelEvaluator.evaluateFile("non-existent.feel")
    result shouldBe Left("File not found: non-existent.feel")
  }
}
