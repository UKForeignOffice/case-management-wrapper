package uk.gov.fco.casemanagement.worker.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

@Configuration
public class MessageQueueReceiverConfig {

    private AmazonSQSAsync amazonSQSAsync;

    private MessageQueueProperties properties;

    @Autowired
    public MessageQueueReceiverConfig(@NonNull AmazonSQSAsync amazonSQSAsync, @NonNull MessageQueueProperties properties) {
        this.amazonSQSAsync = amazonSQSAsync;
        this.properties = properties;
    }

    @Bean
    public AmazonSQSResponder amazonSQSResponder() {
        return AmazonSQSResponderClientBuilder.standard()
                .withAmazonSQS(amazonSQSAsync)
                .withInternalQueuePrefix(properties.getInternalQueuePrefix())
                .build();
    }
}
