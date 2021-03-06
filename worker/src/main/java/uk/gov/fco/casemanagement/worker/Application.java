package uk.gov.fco.casemanagement.worker;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.fco.casemanagement.worker.handler.MessageReceiver;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    private MessageReceiver messageReceiver;

    @Autowired
    public Application(@NonNull MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void run(String... args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
            messageReceiver.stop();
        }));

        messageReceiver.start();
    }
}
