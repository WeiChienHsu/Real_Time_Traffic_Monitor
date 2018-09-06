# Spring Project

```
com.spring.uber
  |
  -- domain - Message
  |
  -- controller - HelloWorldRestController
  |
  -- HelloWorldConfiguration
  -- HelloWorldInitializer


```



## Java Config

### pom.xml

- properties
- dependencies


```xml
    <properties>
        <springframework.version>4.3.0.RELEASE</springframework.version>
    </properties>

    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.springframework/spring-core -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${springframework.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${springframework.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${springframework.version}</version>
        </dependency>
    </dependencies>
```

#### com.spring.uber - HelloWorldInitializer Class

- inherents AbstractAnnotationConfigDispatcherServletInitializer
- getRootConfigClasses();
- getServletConfigClasses();
- getServletMappings();


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

***

## RESTful API

### jackson-databind (for JSON serialize and deserizlize)

```xml
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.library}</version>
        </dependency>
```

### Model (Domain Layer - for business logic)

```java
package com.spring.uber.domain;

public class Message {

    private String name;
    private String text;

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

}
```

### Controller (RESTful API)

- RequestMapping : Tell Servelet Dispatching

```java
package com.spring.uber.controller;

import com.spring.uber.domain.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

    @RequestMapping("/")
    public String welcome() {
        return "Hello Spring Uber";
    }

    @RequestMapping(value = "/hello/{yourName}", method = RequestMethod.GET)
    public Message showMessage(@PathVariable String yourName) {
        Message msg = new Message(yourName, "Hello ");
        return msg;
    }
}
```

### View


***

## Build Java file by MAVEN

```xml
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.2</version>
                    <configuration>
                        <source>9.0</source>
                        <target>1.8</target>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-war-plugin</artifactId>
                    <version>2.4</version>
                    <configuration>
                        <warName>${war.name}</warName>
                        <failOnMissingWebXml>false</failOnMissingWebXml>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
        <finalName>${war.name}</finalName>
    </build>

```