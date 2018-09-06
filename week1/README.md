## Java Config

### pom.xml

```xml
    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.springframework/spring-core -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.3.0.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>4.3.0.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.0.RELEASE</version>
        </dependency>
    </dependencies>

```

#### com.spring.uber - HelloWorldInitializer Class

```java
package com.spring.uber;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/* Java Configuration - need to import library in pom.xml */
public class HelloWorldInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {


    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{HelloWorldConfiguration.class};
    }

    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```

#### com.spring.uber - HelloWorldConfiguration Class

```java
package com.spring.uber;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.spring.uber")
public class HelloWorldConfiguration {

}
```