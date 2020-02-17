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
