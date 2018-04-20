package org.camunda.feel.interpreter

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel._

/**
 * @author Philipp Ossler
 */
class InterpreterListExpressionTest extends FlatSpec with Matchers with FeelIntegrationTest {
  
  "A list" should "be checked with 'some'" in {

    eval("some x in [1,2,3] satisfies x > 2") should be(ValBoolean(true))
    eval("some x in [1,2,3] satisfies x > 3") should be(ValBoolean(false))

    eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
    eval("some x in xs satisfies x > 2", Map("xs" -> List(1,2))) should be(ValBoolean(false))

    eval("some x in [1,2], y in [2,3] satisfies x < y") should be(ValBoolean(true))
    eval("some x in [1,2], y in [1,1] satisfies x < y") should be(ValBoolean(false))
  }

  it should "be checked with 'every'" in {

    eval("every x in [1,2,3] satisfies x >= 1") should be(ValBoolean(true))
    eval("every x in [1,2,3] satisfies x >= 2") should be(ValBoolean(false))

    eval("every x in xs satisfies x >= 1", Map("xs" -> List(1,2,3))) should be(ValBoolean(true))
    eval("every x in xs satisfies x >= 1", Map("xs" -> List(0,1,2,3))) should be(ValBoolean(false))

    eval("every x in [1,2], y in [3,4] satisfies x < y") should be(ValBoolean(true))
    eval("every x in [1,2], y in [2,3] satisfies x < y") should be(ValBoolean(false))
  }

  it should "be processed in a for-expression" in {

    eval("for x in [1,2] return x * 2") should be(ValList(List(
        ValNumber(2),
        ValNumber(4) )))

    eval("for x in [1,2], y in [3,4] return x * y") should be(ValList(List(
        ValNumber(3),
        ValNumber(4),
        ValNumber(6),
        ValNumber(8) )))

    eval("for x in xs return x * 2", Map("xs" -> List(1,2))) should be(ValList(List(
        ValNumber(2),
        ValNumber(4) )))
  }

  it should "be filtered via boolean expression" in {

    eval("[1,2,3,4][item > 2]") should be(ValList(List(
        ValNumber(3), ValNumber(4))))

    eval("xs [item > 2]", Map("xs" -> List(1,2,3,4))) should be(ValList(List(
        ValNumber(3), ValNumber(4))))
  }
  
  it should "be filtered via index" in {
   
    eval("[1,2,3,4][1]") should be(ValNumber(1))
    
    eval("[1,2,3,4][2]") should be(ValNumber(2))
    
    eval("[1,2,3,4][-1]") should be(ValNumber(4))
    
    eval("[1,2,3,4][-2]") should be(ValNumber(3))
    
    eval("[1,2,3,4][5]") should be(ValNull)
    
    eval("[1,2,3,4][-5]") should be(ValNull)
  }  
 
}
