package uk.gov.fco.casemanagement.common.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
public class MessageQueueConfig {

    private final MessageQueueProperties properties;

    @Autowired
    public MessageQueueConfig(MessageQueueProperties properties) {
        this.properties = checkNotNull(properties);
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), null))
                .build();
    }
}
