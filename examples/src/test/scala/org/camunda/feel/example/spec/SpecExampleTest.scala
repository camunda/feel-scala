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
package org.camunda.feel.example.spec

import java.time.LocalDate

import org.camunda.feel.FeelEngine
import org.camunda.feel.example.spec.Context.{Applicant, BalanceSummary, CreditHistoryRecord}
import org.camunda.feel.impl.SpiServiceLoader
import org.scalatest.{FlatSpec, Matchers}

class SpecExampleTest extends FlatSpec with Matchers {

  val feelEngine = new FeelEngine(SpiServiceLoader.loadFunctionProvider,
    SpiServiceLoader.loadValueMapper)

  val context = Map(
    "applicant" -> Applicant(
      maritalStatus = "M",
      monthly = BalanceSummary(
        income = 10000,
        repayments = 2500,
        expenses = 3000
      )
    ),
    "credit_history" -> List(
      CreditHistoryRecord(
        record_date = LocalDate.parse("2008-03-12"),
        event = "home mortgage",
        weight = 100
      ),
      CreditHistoryRecord(
        record_date = LocalDate.parse("2011-04-01"),
        event = "foreclosure warning",
        weight = 150
      )
    )
  )

  "The applicant" should "have a yearly income of 120000" in {

    val result =
      feelEngine.evalExpression("applicant.monthly.income * 12", context)

    result should be(Right(120000))
  }

  it should "have a valid marital status" in {

    val result =
      feelEngine.evalExpression("applicant.maritalStatus in (\"M\",\"S\")",
                                context)

    result should be(Right(true))
  }

  it should "have a total expense of 5500" in {

    val result = feelEngine.evalExpression(
      "sum( [applicant.monthly.repayments, applicant.monthly.expenses] )",
      context)

    result should be(Right(5500))
  }

  "The credit history" should "have a total weight of 150" in {

    val result = feelEngine.evalExpression(
      "sum( credit_history[record_date > date(\"2011-01-01\")].weight )",
      context)

    result should be(Right(150))
  }

  it should "contains no bankruptcy" in {

    val result = feelEngine.evalExpression(
      "some ch in credit_history satisfies ch.event = \"bankruptcy\"",
      context)

    result should be(Right(false))
  }

}
