package org.camunda.feel.api;

public class FeelEngineBuilderJava {
    public FeelEngineApi buildEngine() {
        return FeelEngineBuilder.create().build();
    }
}
