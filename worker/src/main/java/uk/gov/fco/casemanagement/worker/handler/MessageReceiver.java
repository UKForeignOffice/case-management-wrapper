package uk.gov.fco.casemanagement.worker.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.CasebookServiceException;
import uk.gov.fco.casemanagement.worker.service.casebook.CasebookService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * MessageReceiver is responsible for receiving and processing messages. A message
 * will contain a <code>Form</code> object that should be converted for submission
 * to the Case Management System.
 */
@Component
@Slf4j
public class MessageReceiver {

    private static final Integer WAIT_TIMOUT = 5;

    private static final Integer MAX_MESSAGES = 1;

    private AmazonSQS amazonSQS;

    private AmazonSQSResponder amazonSQSResponder;

    private MessageQueueProperties properties;

    private CasebookService casebookService;

    private ObjectMapper objectMapper;

    @Autowired
    public MessageReceiver(@NonNull AmazonSQS amazonSQS, @NonNull AmazonSQSResponder amazonSQSResponder,
                           @NonNull MessageQueueProperties properties, @NonNull CasebookService casebookService,
                           @NonNull ObjectMapper objectMapper) {
        this.amazonSQS = amazonSQS;
        this.amazonSQSResponder = amazonSQSResponder;
        this.properties = properties;
        this.casebookService = casebookService;
        this.objectMapper = objectMapper;
    }

    public void receiveMessage() {
        log.trace("Polling for new message");
        List<Message> messages = amazonSQS.receiveMessage(new ReceiveMessageRequest()
                .withMessageAttributeNames("ResponseQueueUrl")
                .withAttributeNames(QueueAttributeName.All)
                .withQueueUrl(properties.getQueueUrl())
                .withMaxNumberOfMessages(MAX_MESSAGES)
                .withWaitTimeSeconds(WAIT_TIMOUT))
                .getMessages();

        messages.forEach(this::processMessage);
    }

    private void processMessage(Message message) {
        log.debug("Processing message {}", message);

        Form form;
        try {
            form = objectMapper.readValue(message.getBody(), Form.class);
        } catch (IOException e) {
            // TODO: include correlation ID
            throw new MessageReceiverException("Error deserialising form", e);
        }

        String reference;
        try {
            // TODO: send along timestamp from message
            reference = casebookService.createCase(Instant.now(), form);
        } catch (CasebookServiceException e) {
            // TODO: include correlation ID
            throw new MessageReceiverException("Error creating case in casebook", e);
        }
        log.debug("Case created, reference = {}", reference);

        MessageContent requestMessage = MessageContent.fromMessage(message);

        if (amazonSQSResponder.isResponseMessageRequested(requestMessage)) {
            log.debug("Responding with {}", reference);
            amazonSQSResponder.sendResponseMessage(requestMessage, new MessageContent(reference));
        }

        DeleteMessageResult result = amazonSQS.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(properties.getQueueUrl())
                .withReceiptHandle(message.getReceiptHandle()));

        log.debug("Delete message result {}", result);
    }
}
