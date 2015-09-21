package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.parser.FeelParser
import org.joda.time.LocalDate

import com.github.nscala_time.time.Imports._

/**
 * 10.3.2.8 Decision Table
 * The normative notation for decision tables is specified in clause 8. A textual representation using invocation of the decision
 * table built-in function is provided here in order to tie the syntax to the semantics in the same way as is done for the rest of
 * FEEL. Unary tests (grammar rule 17) cannot be mapped to the semantic domain in isolation, and are left as their syntactic
 * forms, indicated by the enclosing single-quotes. For example, the first decision table in Table 26 can be represented
 * textually as
 * decision table(
 * outputs: "Applicant Risk Rating",
 * input expression list: [Applicant Age, Medical History],
 * rule list: [
 * ['>60', '"good"', '"Medium"'],
 * ['>60', '"bad"', '"High"'],
 * ['[25..60]', '-', '"Medium"'],
 * ['<25', '"good"','"Low"'],
 * ['<25', '"bad"', '"Medium"']],
 * hit policy: "Unique")
 * The decision table built-in in clause 10.3.4.6 will compose the unary tests syntax into expressions that can be mapped to the
 * FEEL semantic domain.
 *
 * @author Philipp Ossler
 */
class InterpreterTest extends FlatSpec with Matchers {

  val parser = new FeelParser
  val interpreter = new FeelInterpreter

  "A number" should "compare with '<'" in {

    val exp = parser.parse("< 3")
    (interpreter.test(exp.get)(Context(2))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(3))) should be(ValBoolean(false))
    (interpreter.test(exp.get)(Context(4))) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    val exp = parser.parse("<= 3")
    (interpreter.test(exp.get)(Context(2))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(3))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(4))) should be(ValBoolean(false))
  }

  it should "be in interval '(2 .. 4)'" in {
    
    val exp = parser.parse("(2 .. 4)")
    (interpreter.test(exp.get)(Context(2))) should be (ValBoolean(false))
    (interpreter.test(exp.get)(Context(3))) should be (ValBoolean(true))
    (interpreter.test(exp.get)(Context(4))) should be (ValBoolean(false))
  }
  
  "A date" should "compare with '<'" in {

    val exp = parser.parse("""< date("2015-09-18")""")

    val date = LocalDate.parse("2015-09-18")

    (interpreter.test(exp.get)(Context(date - 1.day))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(date))) should be(ValBoolean(false))
    (interpreter.test(exp.get)(Context(date + 1.day))) should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    val exp = parser.parse("""<= date("2015-09-18")""")

    val date = LocalDate.parse("2015-09-18")

    (interpreter.test(exp.get)(Context(date - 1.day))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(date))) should be(ValBoolean(true))
    (interpreter.test(exp.get)(Context(date + 1.day))) should be(ValBoolean(false))
  }

}