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
import uk.gov.fco.casemanagement.worker.service.casebook.CasebookService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

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

    @PostConstruct
    public void listen() {
        while (true) {
            log.trace("Receiving messages");

            List<Message> messages = amazonSQS.receiveMessage(new ReceiveMessageRequest()
                    .withMessageAttributeNames("ResponseQueueUrl")
                    .withAttributeNames(QueueAttributeName.All)
                    .withQueueUrl(properties.getQueueUrl())
                    .withMaxNumberOfMessages(MAX_MESSAGES)
                    .withWaitTimeSeconds(WAIT_TIMOUT))
                    .getMessages();

            messages.forEach(this::processMessage);
        }
    }

    private void processMessage(Message message) {
        log.debug("Processing message {}", message);

        Form form;
        try {
            form = objectMapper.readValue(message.getBody(), Form.class);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: Throw something else
        }

        String reference = casebookService.createCase(form);
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
