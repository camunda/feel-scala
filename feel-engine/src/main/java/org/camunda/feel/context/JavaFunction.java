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
package org.camunda.feel.context;

import java.util.List;
import java.util.function.Function;

import org.camunda.feel.syntaxtree.Val;

public class JavaFunction
{

    private final List<String> params;
    private final Function<List<Val>, Val> function;
    private final boolean hasVarArgs;

    public JavaFunction(List<String> params, Function<List<Val>, Val> function)
    {
        this(params, function, false);
    }

    public JavaFunction(List<String> params, Function<List<Val>, Val> function, boolean hasVarArgs)
    {
        this.params = params;
        this.function = function;
        this.hasVarArgs = hasVarArgs;
    }

    public List<String> getParams()
    {
        return params;
    }

    public Function<List<Val>, Val> getFunction()
    {
        return function;
    }

    public boolean hasVarArgs()
    {
      return hasVarArgs;
    }

}
