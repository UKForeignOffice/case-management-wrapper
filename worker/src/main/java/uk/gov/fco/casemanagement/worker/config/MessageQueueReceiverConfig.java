package uk.gov.fco.casemanagement.worker.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.fco.casemanagement.common.config.MessageQueueConfig;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

@Configuration
@Import({MessageQueueConfig.class})
public class MessageQueueReceiverConfig {

    private AmazonSQS amazonSQS;

    private MessageQueueProperties properties;

    @Autowired
    public MessageQueueReceiverConfig(@NonNull AmazonSQS amazonSQS, @NonNull MessageQueueProperties properties) {
        this.amazonSQS = amazonSQS;
        this.properties = properties;
    }

    @Bean
    public AmazonSQSResponder amazonSQSResponder() {
        return AmazonSQSResponderClientBuilder.standard()
                .withAmazonSQS(amazonSQS)
                .withInternalQueuePrefix(properties.getInternalQueuePrefix())
                .build();
    }
}
