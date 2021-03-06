package uk.gov.fco.casemanagement.api.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import uk.gov.fco.casemanagement.common.config.MessageQueueConfig;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
@Import({MessageQueueConfig.class})
@Profile("!test")
public class MessageQueueSenderConfig {

    private AmazonSQS amazonSQS;

    private MessageQueueProperties properties;

    @Autowired
    public MessageQueueSenderConfig(@NonNull AmazonSQS amazonSQS, @NonNull MessageQueueProperties properties) {
        this.amazonSQS = amazonSQS;
        this.properties = properties;
    }

    @Bean
    public AmazonSQSRequester amazonSQSRequester() {
        return AmazonSQSRequesterClientBuilder.standard()
                .withAmazonSQS(amazonSQS)
                .withInternalQueuePrefix(properties.getInternalQueuePrefix())
                .build();
    }
}
