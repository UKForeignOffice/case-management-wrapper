package uk.gov.fco.casemanagement.api.config;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class MessageQueueSenderTestConfig {

    @Bean
    public AmazonSQSRequester amazonSQSRequester() {
        return mock(AmazonSQSRequester.class);
    }
}
