--
title: Engine Reference: Function Provider SPI
---

## Function Provider SPI

Functions can be invoked in expressions and unary tests. The engine includes some predefined [built-in functions](feel-built-in-functions).

Own functions can be defined in two ways:
* declaring them in an expression (e.g. a context)
* via the function provider SPI

Using the SPI, the function can be implemented in Scala/Java and is not limited by FEEL. So, it's possible to use language features or libraries.  

### Implement a Function using Scala

Create a sub-class of `org.camunda.feel.spi.CustomFunctionProvider` and implement the method `getFunction()` which returns the function for the given name. If a function can have different parameters (i.e. different parameter count) then override `getFunctions()` instead.

```scala
class CustomScalaFunctionProvider extends CustomFunctionProvider {

  def getFunction(name: String): Option[ValFunction] = functions.get(name)

  val functions: Map[String, ValFunction] = Map(
    "incr" -> ValFunction(
      params = List("x"),
      invoke = { case List(ValNumber(x)) => ValNumber(x + 1) }
    )
  )

}
```

The function must be of type `ValFunction`. It contains
* `params` - list of the named parameters of the function
* `invoke` - business logic as function which takes the arguments and returns the result. The order of the arguments is defined by the parameter list.  
* `hasVarArgs` - if `true` the function can have variable arguments for the last parameter. The last argument is of type list. 

### Implement a Function using Java

Using Java, create a sub-class of `org.camunda.feel.spi.JavaFunctionProvider` instead. It is equal to the Scala one but uses more Java-like classes. 

```java
public class CustomJavaFunctionProvider extends JavaFunctionProvider
{
    private static final Map<String, JavaFunction> functions = new HashMap<>();

    static {
    
        final JavaFunction function = new JavaFunction(Arrays.asList("x"), args -> {
            final ValNumber arg = (ValNumber) args.get(0);

            int x = arg.value().intValue();

            return new ValNumber(BigDecimal.valueOf(x - 1));
        });

        functions.put("decr", function);
    }

    @Override
    public Optional<JavaFunction> resolveFunction(String functionName)
    {
      return Optional.ofNullable(functions.get(functionName));
    }

}
```

### Register the Function

Depending how the FEEL engine is used, the function provider can be passed directly on creation, or is loaded via Java ServiceLoader mechanism. 

In the second case, create a new file `org.camunda.feel.spi.CustomFunctionProvider` in the folder `META-INF/services/`. It must contain all function providers by their full qualified name.

```
org.camunda.feel.example.spi.CustomScalaFunctionProvider
org.camunda.feel.example.spi.CustomJavaFunctionProvider
```
