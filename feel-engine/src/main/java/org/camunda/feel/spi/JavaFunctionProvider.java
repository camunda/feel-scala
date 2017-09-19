/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.feel.spi;

import java.util.Optional;

import org.camunda.feel.interpreter.Val;
import org.camunda.feel.interpreter.ValFunction;

import scala.Function1;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;
import scala.runtime.AbstractFunction1;

public interface JavaFunctionProvider extends CustomFunctionProvider
{
    Optional<JavaFunction> resolveFunction(String functionName, int argCount);

    @Override
    default Option<ValFunction> getFunction(String functionName, int argCount)
    {
        final Optional<JavaFunction> function = resolveFunction(functionName, argCount);

        if (function == null || !function.isPresent())
        {
            return Option.empty();
        }
        else
        {
            return Option.apply(asFunction(function.get()));
        }
    }

    default ValFunction asFunction(final JavaFunction function)
    {
        final List<String> paramList = JavaConverters.asScalaBufferConverter(function.getParams()).asScala().toList();

        final Function1<List<Val>, Val> f = new AbstractFunction1<List<Val>, Val>()
        {
            public Val apply(List<Val> args) {

                final java.util.List<Val> argList = JavaConverters.<Val> bufferAsJavaListConverter(args.toBuffer()).asJava();

                return function.getFunction().apply(argList);
            };

        };

        return new ValFunction(paramList, f, function.isInputVariableRequired());
    }

}
