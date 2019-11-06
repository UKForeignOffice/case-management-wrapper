package uk.gov.fco.casemanagement.worker;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.gov.fco.casemanagement.common.config.MessageQueueConfig;
import uk.gov.fco.casemanagement.worker.handler.MessageReceiver;
import uk.gov.fco.casemanagement.worker.handler.MessageReceiverException;

@SpringBootApplication
@Import({MessageQueueConfig.class})
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    private MessageReceiver messageReceiver;

    private boolean running = true;

    @Autowired
    public Application(@NonNull MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void run(String... args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
            running = false;
        }));

        while (running) {
            try {
                messageReceiver.receiveMessage();
            } catch (MessageReceiverException e) {
                log.error("Error receiving message", e);
            }
        }
    }
}
