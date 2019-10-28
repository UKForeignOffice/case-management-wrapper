package uk.gov.fco.casemanagement.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.gov.fco.casemanagement.common.config.MessageQueueConfig;

@SpringBootApplication
@Import({MessageQueueConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
