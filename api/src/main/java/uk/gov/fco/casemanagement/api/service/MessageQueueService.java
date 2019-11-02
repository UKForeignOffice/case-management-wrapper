package uk.gov.fco.casemanagement.api.service;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;
import uk.gov.fco.casemanagement.common.domain.Form;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    private ObjectMapper objectMapper;

    @Autowired
    public MessageQueueService(@NonNull AmazonSQSRequester amazonSQSRequester,
                               @NonNull MessageQueueProperties properties, @NonNull ObjectMapper objectMapper) {
        this.amazonSQSRequester = amazonSQSRequester;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String send(Form form) throws MessageQueueTimeoutException {

        String messageBody;
        try {
            messageBody = objectMapper.writeValueAsString(form);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        SendMessageRequest request = new SendMessageRequest()
                .withMessageDeduplicationId(UUID.randomUUID().toString())
                .withMessageGroupId(UUID.randomUUID().toString())
                .withQueueUrl(properties.getQueueUrl())
                .withMessageBody(messageBody);

        try {
            Message response = amazonSQSRequester.sendMessageAndGetResponse(request, properties.getRequestTimeout(),
                    TimeUnit.MILLISECONDS);
            return response.getBody();
        } catch (TimeoutException e) {
            throw new MessageQueueTimeoutException(e);
        }
    }
}
