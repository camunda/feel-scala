package org.camunda.feel.interpreter

import org.camunda.feel._
import org.camunda.feel.parser.FeelParser

import org.scalatest.FlatSpec
import org.scalatest.Matchers

/**
 * @author Philipp Ossler
 */
class InterpreterSimpleUnaryTest extends FlatSpec with Matchers {

  val interpreter = new FeelInterpreter

  "A number" should "compare with '<'" in {

    eval(2, "< 3") should be(ValBoolean(true))
    eval(3, "< 3") should be(ValBoolean(false))
    eval(4, "< 3") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(2, "<= 3") should be(ValBoolean(true))
    eval(3, "<= 3") should be(ValBoolean(true))
    eval(4, "<= 3") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(2, "> 3") should be(ValBoolean(false))
    eval(3, "> 3") should be(ValBoolean(false))
    eval(4, "> 3") should be(ValBoolean(true))
  }
  
  it should "compare with '>='" in {

    eval(2, ">= 3") should be(ValBoolean(false))
    eval(3, ">= 3") should be(ValBoolean(true))
    eval(4, ">= 3") should be(ValBoolean(true))
  }

  it should "be equal to another number" in {
    
    eval(2, "3") should be (ValBoolean(false))
    eval(3, "3") should be (ValBoolean(true))
  }
  
  it should "be in interval '(2..4)'" in {

    eval(2, "(2..4)") should be(ValBoolean(false))
    eval(3, "(2..4)") should be(ValBoolean(true))
    eval(4, "(2..4)") should be(ValBoolean(false))
  }

  it should "be in interval '[2..4]'" in {

    eval(2, "[2..4]") should be(ValBoolean(true))
    eval(3, "[2..4]") should be(ValBoolean(true))
    eval(4, "[2..4]") should be(ValBoolean(true))
  }
  
  it should "be in '2,3'" in {
    
    eval(2, "2,3") should be (ValBoolean(true))
    eval(3, "2,3") should be (ValBoolean(true))
    eval(4, "2,3") should be (ValBoolean(false))
  }
  
  it should "be not equal 'not(3)'" in {
    
    eval(2, "not(3)") should be (ValBoolean(true))
    eval(3, "not(3)") should be (ValBoolean(false))
    eval(4, "not(3)") should be (ValBoolean(true))
  }
  
  it should "be not in 'not(2,3)'" in {
    
    eval(2, "not(2,3)") should be (ValBoolean(false))
    eval(3, "not(2,3)") should be (ValBoolean(false))
    eval(4, "not(2,3)") should be (ValBoolean(true))
  }
  
  it should "compare to a variable (qualified name)" in {
    
    eval(2,"var", variables = Map("var" -> 3)) should be (ValBoolean(false)) 
    eval(3,"var", variables = Map("var" -> 3)) should be (ValBoolean(true)) 
    
    eval(2,"q.var", variables = Map("q.var" -> 3)) should be (ValBoolean(false)) 
    eval(3,"q.var", variables = Map("q.var" -> 3)) should be (ValBoolean(true))
    
    eval(2,"< var", variables = Map("var" -> 3)) should be (ValBoolean(true)) 
    eval(3,"< var", variables = Map("var" -> 3)) should be (ValBoolean(false))
  }
  
  "A string" should "be equal to another string" in {
    
    eval("a", """ "b" """) should be (ValBoolean(false))
    eval("b", """ "b" """) should be (ValBoolean(true))
  }
  
  it should """be in '"a","b"' """ in {
    
    eval("a", """ "a","b" """) should be (ValBoolean(true))
    eval("b", """ "a","b" """) should be (ValBoolean(true))
    eval("c", """ "a","b" """) should be (ValBoolean(false))
  }
  
  "A boolean" should "be equal to another boolean" in {
    
    eval(false, "true") should be (ValBoolean(false))
    eval(true, "false") should be (ValBoolean(false))
    
    eval(false, "false") should be (ValBoolean(true))
    eval(true, "true") should be (ValBoolean(true))
  }

  "A date" should "compare with '<'" in {

    eval(date("2015-09-17"), """< date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """< date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-19"), """< date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '<='" in {

    eval(date("2015-09-17"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """<= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """<= date("2015-09-18")""") should be(ValBoolean(false))
  }

  it should "compare with '>'" in {

    eval(date("2015-09-17"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """> date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-19"), """> date("2015-09-18")""") should be(ValBoolean(true))
  }
  
  it should "compare with '>='" in {

    eval(date("2015-09-17"), """>= date("2015-09-18")""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """>= date("2015-09-18")""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """>= date("2015-09-18")""") should be(ValBoolean(true))
  }
  
  it should "be equal to another date" in {
    
    eval(date("2015-09-17"), """date("2015-09-18")""") should be (ValBoolean(false))
    eval(date("2015-09-18"), """date("2015-09-18")""") should be (ValBoolean(true))
  }

  it should """be in interval '(date("2015-09-17")..date("2015-09-19")]'""" in {

    eval(date("2015-09-17"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(false))
    eval(date("2015-09-18"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """(date("2015-09-17")..date("2015-09-19"))""") should be(ValBoolean(false))
  }

  it should """be in interval '[date("2015-09-17")..date("2015-09-19")]'""" in {

    eval(date("2015-09-17"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
    eval(date("2015-09-18"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
    eval(date("2015-09-19"), """[date("2015-09-17")..date("2015-09-19")]""") should be(ValBoolean(true))
  }
  
  "A time" should "compare with '<'" in {
    
    eval(time("08:31:14"), """< time("10:00:00")""") should be(ValBoolean(true))
    eval(time("10:10:00"), """< time("10:00:00")""") should be(ValBoolean(false))
    eval(time("11:31:14"), """< time("10:00:00")""") should be(ValBoolean(false))
  }
  
  it should "be equal to another time" in {
    
    eval(time("08:31:14"), """time("10:00:00")""") should be(ValBoolean(false))
    eval(time("08:31:14"), """time("08:31:14")""") should be(ValBoolean(true))
  }
  
  it should """be in interval '[time("08:00:00")..time("10:00:00")]'""" in {
    
    eval(time("07:45:10"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(false))
    eval(time("09:15:20"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(true))
    eval(time("11:30:30"), """[time("08:00:00")..time("10:00:00")]""") should be(ValBoolean(false))
  }
  
  "A duration" should "compare with '<'" in {
    
    eval(duration("P1DT4H"), """< duration("P2DT4H")""") should be(ValBoolean(true))
    eval(duration("P2DT4H"), """< duration("P2DT4H")""") should be(ValBoolean(false))
    eval(duration("P2DT8H"), """< duration("P2DT4H")""") should be(ValBoolean(false))
  }
  
  it should "be equal to another duration" in {
    
    eval(duration("P1DT4H"), """duration("P2DT4H")""") should be(ValBoolean(false))
    eval(duration("P2DT4H"), """duration("P2DT4H")""") should be(ValBoolean(true))
  }
  
  it should """be in interval '[duration("P1D")..duration("P2D")]'""" in {
    
    eval(duration("PT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(false))
    eval(duration("P1DT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(true))
    eval(duration("P2DT4H"), """[duration("P1D")..duration("P2D")]""") should be(ValBoolean(false))
  }
  
  "An empty expression ('-')" should "be always true" in {
    
    eval(None, "-") should be (ValBoolean(true))
  }
  
  private def eval(input: Any, expression: String, variables: Map[String, Any] = Map()): Val = {
    val exp = FeelParser.parseSimpleUnaryTest(expression)
    interpreter.eval(exp.get)(Context(variables + ( Context.inputKey -> input)))
  }

  private def date(date: String): Date = date

  private def time(time: String): Time = time
  
  private def duration(duration: String): Duration = duration
  
}