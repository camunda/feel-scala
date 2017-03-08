# FEEL Engine Plugin

Provide an integration of the FEEL engine for Camunda BPM as Process Engine Plugin. It replace the default FEEL engine factory and set all default DMN expression languages to FEEL.

## How to use it?

Add the plugin including the FEEL engine to your project by copying the jar file or adding the project as dependency.

```xml
<dependency>
  <groupId>org.camunda.bpm.extension.feel.scala</groupId>
  <artifactId>feel-engine-plugin</artifactId>
  <version>${VERSION}</version>
</dependency>
```

Then, add the plugin in your process engine configuration.

```xml
<bean id="processEngineConfiguration" class="org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration">
  
  <property name="processEnginePlugins">
      <list>
        <bean class="org.camunda.feel.CamundaFeelEnginePlugin" />
      </list>
    </property>
    
  <!-- more configs -->
</bean>
```

You can also change a default expression language by overridding the property.

```xml
<bean id="processEngineConfiguration" class="org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration">
  
  <property name="processEnginePlugins">
      <list>
        <bean class="org.camunda.feel.CamundaFeelEnginePlugin">
          <property name="defaultInputExpressionExpressionLanguage" value="groovy" />
        </bean>
      </list>
    </property>
    
  <!-- more configs -->
</bean>
```

## How to build it?

You can build the project with [SBT](http://www.scala-sbt.org) or [Maven](http://maven.apache.org).

### Using SBT

Run the tests with
```
sbt test
```

Build the jar including all dependencies with
```
sbt assemply
```

### Using Maven

Run the tests with
```
mvn test
```

Build the jar including all dependencies with
```
mvn install
```
