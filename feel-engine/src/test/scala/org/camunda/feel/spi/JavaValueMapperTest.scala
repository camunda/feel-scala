package org.camunda.feel.spi

import org.camunda.feel.interpreter._
import org.scalatest.{FlatSpec, Matchers}

class JavaValueMapperTest extends FlatSpec with Matchers {

  val valueMapper = new JavaValueMapper()

  "The JavaValueMapper" should "return number as Java Double" in {

    valueMapper.unpackVal(ValNumber(2.4)) should be(new java.lang.Double(2.4))
  }

  it should "return list as Java List" in {

    val list = new java.util.ArrayList[String]
    list.add("a")
    list.add("b")

    valueMapper.unpackVal(ValList(List(ValString("a"), ValString("b")))) should be(list)
  }

  it should "return context as Java Map" in {

    val map = new java.util.HashMap[String, String]
    map.put("x", "1")
    map.put("y", "2")

    valueMapper.unpackVal(ValContext(DefaultContext(variables = Map("x" -> "1", "y" -> "2")))) should be(map)
  }

}
