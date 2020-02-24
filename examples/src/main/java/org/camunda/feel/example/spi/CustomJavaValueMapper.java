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
package org.camunda.feel.example.spi;

import java.util.Optional;
import java.util.function.Function;

import org.camunda.feel.syntaxtree.Val;
import org.camunda.feel.syntaxtree.ValNumber;
import org.camunda.feel.syntaxtree.ValString;
import org.camunda.feel.valuemapper.JavaCustomValueMapper;

public class CustomJavaValueMapper extends JavaCustomValueMapper {

  @Override
  public Optional<Val> toValue(Object x, Function<Object, Val> innerValueMapper) {
    if (x instanceof Custom) {
      final Custom c = (Custom) x;
      return Optional.of(new ValString(c.getName()));

    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Object> unpackValue(Val value, Function<Val, Object> innerValueMapper) {
    if (value instanceof ValNumber) {
      final ValNumber number = (ValNumber) value;
      return Optional.of(number.value().doubleValue()); // map BigDecimal to Double

    } else {
      return Optional.empty();
    }
  }

  @Override
  public int priority() {
    return 1;
  }

  interface Custom {

    String getName();
  }
}
