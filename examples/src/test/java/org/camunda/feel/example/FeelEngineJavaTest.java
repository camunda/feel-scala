package org.camunda.feel.example;

import org.camunda.feel.FeelEngine;
import org.camunda.feel.spi.JavaValueMapper;
import org.camunda.feel.spi.SpiServiceLoader;
import scala.util.Either;

import java.util.Collections;
import java.util.Map;

public class FeelEngineJavaTest {

    public static void main(String[] args) {

        // default
        final FeelEngine engine = new FeelEngine();
        // with function provider and value mapper
        final FeelEngine engine2 =
                new FeelEngine.Builder()
                        .valueMapper(new JavaValueMapper())
                        .functionProvider(SpiServiceLoader.loadFunctionProvider())
                        .build();

        final Map<String, Object> variables = Collections.singletonMap("x", 2);
        final Either<FeelEngine.Failure, Object> result = engine2.evalExpression("x + 1", variables);

        if (result.isRight()) {
            final Object value = result.right().get();
            System.out.println("result is " + value);
        } else {
            final FeelEngine.Failure failure = result.left().get();
            throw new RuntimeException(failure.message());
        }

    }

}
