# Spring Boot


## Configuaion of POM.xml

```xml
    <name>hello-world-demo</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.0.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

***

## Java Main Class (Application) - No need for imitializer

- @SpringBootApplication

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication /* Enable AutoConfiguration and other stuffs */
public class HelloWorldDemoApplication {

    /* Main method to tell the entrance */
    public static void main(String[] args) {
        /* Find the Application Class */
        SpringApplication.run(HelloWorldDemoApplication.class, args);
    }

}

```

## RestController Class

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

    @GetMapping("/")
    public String helloWorld() {
        return "Hello Spring Boot!";
    }
}
```

***