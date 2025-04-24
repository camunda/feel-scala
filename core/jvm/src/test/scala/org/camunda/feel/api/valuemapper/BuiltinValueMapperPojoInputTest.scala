package org.camunda.feel.api.valuemapper

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.Context
import org.camunda.feel.impl.JavaValueMapper
import org.camunda.feel.valuemapper.ValueMapper.CompositeValueMapper
import org.camunda.feel.valuemapper.{SimpleBooleanTestPojo, SimpleTestPojo}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BuiltinValueMapperPojoInputTest  extends AnyFlatSpec  with Matchers{
  val engine =
    new FeelEngine(null, CompositeValueMapper(List(new JavaValueMapper())))

  it should "read value from object getter" in {

    val variables = Map("foo" -> new SimpleTestPojo("foo"))

    engine
      .evalExpression(
        "foo.getMyString() = \"foo\"",
        context = Context.StaticContext(variables, null)
      )
      .getOrElse() shouldBe true
  }

  it should "read value from object getter with attribute notation" in {

    val variables = Map("foo" -> new SimpleTestPojo("foo"))

    engine
      .evalExpression("foo.myString = \"foo\"", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object 'is...' getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.isEnabled() = true", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.enabled = true", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'false' from object getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setEnabled(false)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.enabled = false", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setDisabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.getDisabled() = true", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read boolean 'true' from object getter attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setDisabled(true)

    val variables = Map("foo" -> pojo)

    engine
      .evalExpression("foo.disabled = true", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "read string from object 'is...' getter" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setFoo("baz")

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.isFoo() = \"baz\"", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe true
  }

  it should "not read string from object 'is...' getter with attribute notation" in {
    val pojo = new SimpleBooleanTestPojo()
    pojo.setFoo("baz")

    val variables = Map("bar" -> pojo)

    engine
      .evalExpression("bar.foo = \"baz\"", context = Context.StaticContext(variables, null))
      .getOrElse() shouldBe false
  }
}
