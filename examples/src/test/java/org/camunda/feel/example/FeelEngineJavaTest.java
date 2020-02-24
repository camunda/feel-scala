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
package org.camunda.feel.example;

import java.util.Collections;
import java.util.Map;

import org.camunda.feel.FeelEngine;
import org.camunda.feel.impl.SpiServiceLoader;
import scala.util.Either;

public class FeelEngineJavaTest {

    public static void main(String[] args) {

        final FeelEngine engine =
                new FeelEngine.Builder()
                        .valueMapper(SpiServiceLoader.loadValueMapper())
                        .functionProvider(SpiServiceLoader.loadFunctionProvider())
                        .build();

        final Map<String, Object> variables = Collections.singletonMap("x", 2);
        final Either<FeelEngine.Failure, Object> result = engine.evalExpression("x + 1", variables);

        if (result.isRight()) {
            final Object value = result.right().get();
            System.out.println("result is " + value);
        } else {
            final FeelEngine.Failure failure = result.left().get();
            throw new RuntimeException(failure.message());
        }

    }

}
