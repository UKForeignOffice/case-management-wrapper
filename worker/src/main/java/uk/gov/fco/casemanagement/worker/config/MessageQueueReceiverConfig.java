package uk.gov.fco.casemanagement.worker.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
public class MessageQueueReceiverConfig {

    private AmazonSQSAsync amazonSQSAsync;

    @Autowired
    public MessageQueueReceiverConfig(AmazonSQSAsync amazonSQSAsync) {
        this.amazonSQSAsync = checkNotNull(amazonSQSAsync);
    }

    @Bean
    public AmazonSQSResponder amazonSQSResponder() {
        return AmazonSQSResponderClientBuilder.standard()
                .withAmazonSQS(amazonSQSAsync)
                .build();
    }
}
