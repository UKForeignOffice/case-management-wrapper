package uk.gov.fco.casemanagement.api.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSRequester;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

import static org.mockito.Mockito.mock;

@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
@Profile("test")
public class MessageQueueSenderTestConfig {

    @Bean
    public AmazonSQS amazonSQS() {
        return mock(AmazonSQS.class);
    }

    @Bean
    public AmazonSQSRequester amazonSQSRequester() {
        return mock(AmazonSQSRequester.class);
    }
}
