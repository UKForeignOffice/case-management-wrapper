package uk.gov.fco.casemanagement.api.service;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.glassfish.jersey.internal.guava.Preconditions.checkNotNull;

/**
 * This service wraps up all message queue communication.
 * <p>
 * Implemented to connect to an SQS queue. If needed could extract an interface and
 * configure different queue implementations depending on deployment environment.
 */
@Service
public class MessageQueueService {

    private AmazonSQSRequester amazonSQSRequester;

    private MessageQueueProperties properties;

    @Autowired
    public MessageQueueService(AmazonSQSRequester amazonSQSRequester, MessageQueueProperties properties) {
        this.amazonSQSRequester = checkNotNull(amazonSQSRequester);
        this.properties = checkNotNull(properties);
    }

    public String send(String message) throws MessageQueueTimeoutException {
        SendMessageRequest request = new SendMessageRequest()
                .withMessageBody(message)
                .withQueueUrl(properties.getUrl());

        try {
            Message response = amazonSQSRequester.sendMessageAndGetResponse(request, properties.getRequestTimeout(),
                    TimeUnit.MILLISECONDS);
            return response.getBody();
        } catch (TimeoutException e) {
            throw new MessageQueueTimeoutException(e);
        }
    }

}
