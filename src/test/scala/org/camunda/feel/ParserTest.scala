package org.camunda.feel

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.LessThan
import org.camunda.feel.parser.ConstNumber
import org.camunda.feel.parser.Exp
import org.scalatest.matchers.Matcher


/**
 * @author Philipp Ossler
 */
class ParserTest extends FlatSpec with Matchers {
  
  val parser = new FeelParser
  
  "A parser" should "parse '< 3'" in {
    parser.parse("< 3").get should be (LessThan(ConstNumber(3)))
  }
  
  it should "not parse '< NaN'" in {
    parser.parse("< NaN").successful should be (false) 
  }
  
  it should "not parse '< true'" in {
    parser.parse("< true").successful should be (false)
  }
  
}
