# FEEL Engine Plugin

Provide an integration of the FEEL engine for Camunda BPM as Process Engine Plugin. It replace the default FEEL engine factory and set all default DMN expression languages to FEEL.

## How to use it?

Add the plugin including the FEEL engine to your project by copying the [jar file](https://github.com/camunda/feel-scala/releases) _(feel-engine-plugin-${VERSION}-complete.jar)_ or adding the project as dependency.

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

> Using Camunda BPM < 7.7.0

You have to override the default expression language of input entries and set it to `feel`. This creates the FEEL engine by the factory instead of using it as script engine. 

```xml
  <property name="processEnginePlugins">
      <list>
        <bean class="org.camunda.feel.CamundaFeelEnginePlugin">
          <property name="defaultInputEntryExpressionLanguage" value="feel" />
        </bean>
      </list>
    </property>
```

**Using Camunda-Spring-Boot-Starter**

Add a configuration bean with the process engine plugin to your project.

```java
@Configuration
public class BpmPlatformConfiguration {

  @Bean
  public static ProcessEnginePlugin feelScalaPlugin() {
    return new CamundaFeelEnginePlugin();
  }
}
```

**Using Camunda JBoss AS / Wildfly Distribution**

Download the [module archive](https://github.com/camunda/feel-scala/releases) _(feel-engine-plugin-${VERSION}-jboss_wildfly_module.zip)_ and follow the instructions in the _README_ file.

## How to build it?

You can build the project with [SBT](http://www.scala-sbt.org) or [Maven](http://maven.apache.org).

### Using SBT

In the root directory:

Run the tests with
```
sbt plugin/test
```

Build the jar including all dependencies with
```
sbt plugin/assemply
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
