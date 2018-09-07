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
