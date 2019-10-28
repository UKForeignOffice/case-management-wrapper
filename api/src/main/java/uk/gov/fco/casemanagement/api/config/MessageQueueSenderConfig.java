package uk.gov.fco.casemanagement.api.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
public class MessageQueueSenderConfig {

    private AmazonSQSAsync amazonSQSAsync;

    @Autowired
    public MessageQueueSenderConfig(AmazonSQSAsync amazonSQSAsync) {
        this.amazonSQSAsync = checkNotNull(amazonSQSAsync);
    }

    @Bean
    public AmazonSQSRequester amazonSQSRequester() {
        return AmazonSQSRequesterClientBuilder.standard()
                .withAmazonSQS(amazonSQSAsync)
                .build();
    }
}
