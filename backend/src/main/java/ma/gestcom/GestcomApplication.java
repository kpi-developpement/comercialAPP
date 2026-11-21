package ma.gestcom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GestcomApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestcomApplication.class, args);
    }
}
