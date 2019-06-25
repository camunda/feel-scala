# FEEL Engine Plugin

An integration of the FEEL engine for Camunda BPM as Process Engine Plugin. It replace the default Camunda FEEL engine and set all DMN default expression languages to FEEL.

## Usage

1) Add the plugin as dependency to your project:

    ```xml
    <dependency>
      <groupId>org.camunda.bpm.extension.feel.scala</groupId>
      <artifactId>feel-engine-plugin</artifactId>
      <version>${VERSION}</version>
    </dependency>
    ```

    Or copy the [jar file](https://github.com/camunda/feel-scala/releases) _(feel-engine-plugin-${VERSION}-complete.jar)_ directly.

2) Add the plugin in your process engine configuration.

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

### Change the Default Expression Language

All default expression languages are set to `feel`. This can be changed by overridding the property.

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

The default expression language of input entries must be set to `feel`. This creates the FEEL engine by the factory instead of using it as script engine. 

```xml
  <property name="processEnginePlugins">
      <list>
        <bean class="org.camunda.feel.CamundaFeelEnginePlugin">
          <property name="defaultInputEntryExpressionLanguage" value="feel" />
        </bean>
      </list>
    </property>
```

### Using the Camunda-Spring-Boot-Starter

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

If you don't have the dependency to the SPIN process engine plugin already in your dependencies, add this depenedency as well:

```xml
<dependency>
  <groupId>org.camunda.bpm</groupId>
  <artifactId>camunda-engine-plugin-spin</artifactId>
</dependency>
```
