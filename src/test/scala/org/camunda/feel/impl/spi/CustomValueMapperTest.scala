/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.impl.spi

import org.camunda.feel.FeelEngine
import org.camunda.feel.context.{Context, CustomContext, VariableProvider}
import org.camunda.feel.impl._
import org.camunda.feel.syntaxtree._
import org.camunda.feel.valuemapper.{CustomValueMapper, ValueMapper}
import org.scalatest.{FlatSpec, Matchers}

class CustomValueMapperTest extends FlatSpec with Matchers {

  trait Enum {
    def items: Seq[Enumerated]
  }

  trait Enumerated {
    def id: String
  }

  case class Language(val id: String) extends Enumerated

  object Language extends Enum {

    object DE extends Language("DE")

    object EN extends Language("EN")

    def items = Seq(DE, EN)
  }

  case class Property[T](val name: String, var value: Option[T])

  trait DomainObject {
    def properties: Map[String, Property[_]]
  }

  class Person extends DomainObject {
    val name = Property[String]("name", None)
    val language = Property[Language]("language", None)
    val properties = Map((name.name -> name), (language.name -> language))
  }

  class MyCustomContext(val d: DomainObject) extends CustomContext {
    override val variableProvider = new VariableProvider {
      override def getVariable(name: String): Option[Any] =
        d.properties.get(name).map(_.value)

      override def keys: Iterable[String] = d.properties.keys
    }
  }

  class MyCustomValueMapper extends CustomValueMapper {

    override def toVal(x: Any, innerValueMapper: Any => Val): Option[Val] = {
      x match {
        case e: Enumerated => Some(ValString(e.id))

        case e: Enum =>
          Some(
            ValContext(
              Context.StaticContext(
                e.items.map(e => e.id -> innerValueMapper(e)).toMap)
            )
          )

        case d: DomainObject =>
          Some(
            ValContext(new MyCustomContext(d))
          )

        case _ => None
      }
    }

    override def unpackVal(value: Val,
                           innerValueMapper: Val => Any): Option[Any] =
      None
  }

  val valueMapper: ValueMapper =
    ValueMapper.CompositeValueMapper(
      List(DefaultValueMapper.instance, new MyCustomValueMapper))

  val engine = new FeelEngine(valueMapper = valueMapper)

  "A CustomValueMapper" should "convert from String" in {
    valueMapper.toVal("foo") should be(ValString("foo"))
  }

  it should "convert from Int" in {
    valueMapper.toVal(4: Int) should be(ValNumber(4))
  }

  it should "convert from custom value (enumerated)" in {
    valueMapper.toVal(Language.DE) should be(ValString("DE"))
  }

  it should "convert custom context (enumeration)" in {
    valueMapper.toVal(Language).getClass should be(classOf[ValContext])
    valueMapper
      .toVal(Language)
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables("DE") should be(ValString("DE"))
  }

  it should "convert custom context (domain object)" in {

    val person = new Person
    person.language.value = Some(Language.EN)

    valueMapper.toVal(person) shouldBe a[ValContext]
    valueMapper
      .toVal(person)
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariables("language") should be(Some(Language.EN))

    valueMapper
      .toVal(person)
      .asInstanceOf[ValContext]
      .context
      .variableProvider
      .getVariable("not-here") should be(None)
  }

  it should "allow domain expressions" in {

    val person = new Person
    person.name.value = Some("Tom")
    person.language.value = Some(Language.EN)

    val context = Map("p" -> person, "Language" -> Language)

    engine.evalExpression("p.name", context) should be(Right("Tom"))
    engine.evalExpression("p.language", context) should be(Right("EN"))
    engine.evalExpression("p.language = Language.EN", context) should be(
      Right(true))
    engine.evalExpression("p.language = Language.DE", context) should be(
      Right(false))
  }

}
