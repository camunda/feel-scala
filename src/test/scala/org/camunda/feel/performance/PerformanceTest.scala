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
package org.camunda.feel.performance

import org.scalatest.Tag

/** Tag for performance tests.
  *
  * Can be used to tag individual tests:
  * {{{
  * "expensive operation" should "complete" taggedAs(PerformanceTest) in { ... }
  * }}}
  *
  * To run only performance tests:
  * {{{
  * mvn test -DtagsToInclude=org.camunda.feel.PerformanceTest
  * }}}
  *
  * To exclude performance tests:
  * {{{
  * mvn test -DtagsToExclude=org.camunda.feel.PerformanceTest
  * }}}
  */
object PerformanceTest extends Tag("org.camunda.feel.PerformanceTest") {

  /** Trait to tag all tests in a suite as performance tests.
    *
    * Mix this trait into any test class to automatically tag all its tests:
    * {{{
    * class MyPerformanceTest extends AnyFlatSpec with PerformanceTestSuite { ... }
    * }}}
    *
    * This is a pure Scala solution compatible with ScalaJS.
    */
  trait Suite extends org.scalatest.Suite {

    abstract override def tags: Map[String, Set[String]] = {
      val performanceTag = PerformanceTest.name
      // Add the PerformanceTest tag to every test in this suite
      val allTestsTagged = testNames.map(name => name -> Set(performanceTag)).toMap
      // Merge with any existing tags from parent
      super.tags.foldLeft(allTestsTagged) { case (acc, (testName, existingTags)) =>
        acc.updated(testName, acc.getOrElse(testName, Set.empty) ++ existingTags)
      }
    }
  }
}
