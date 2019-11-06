package uk.gov.fco.casemanagement.common.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSTemporaryQueuesClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
@Profile("!test")
public class MessageQueueConfig {

    private final MessageQueueProperties properties;

    @Autowired
    public MessageQueueConfig(MessageQueueProperties properties) {
        this.properties = checkNotNull(properties);
    }

    private AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), null))
                .build();
    }

    @Bean
    public AmazonSQS amazonSQSTemporaryQueuesClient() {
        return AmazonSQSTemporaryQueuesClientBuilder.standard()
                .withAmazonSQS(amazonSQS())
                .withQueuePrefix(properties.getInternalQueuePrefix())
                .build();
    }
}
