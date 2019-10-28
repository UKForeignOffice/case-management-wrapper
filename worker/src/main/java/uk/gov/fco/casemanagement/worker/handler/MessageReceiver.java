package uk.gov.fco.casemanagement.worker.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    private static final Integer WAIT_TIMOUT = 5;

    private static final Integer MAX_MESSAGES = 1;

    private AmazonSQS amazonSQS;

    private AmazonSQSResponder amazonSQSResponder;

    private MessageQueueProperties properties;

    @Autowired
    public MessageReceiver(AmazonSQS amazonSQS, AmazonSQSResponder amazonSQSResponder, MessageQueueProperties properties) {
        this.amazonSQS = checkNotNull(amazonSQS);
        this.amazonSQSResponder = checkNotNull(amazonSQSResponder);
        this.properties = checkNotNull(properties);
    }

    @PostConstruct
    public void listen() {
        while (true) {
            LOGGER.debug("Receiving messages");

            List<Message> messages = amazonSQS.receiveMessage(new ReceiveMessageRequest()
                    .withQueueUrl(properties.getUrl())
                    .withMaxNumberOfMessages(MAX_MESSAGES)
                    .withWaitTimeSeconds(WAIT_TIMOUT))
                    .getMessages();

            messages.forEach(this::processMessage);
        }
    }

    private void processMessage(Message message) {
        LOGGER.debug("Processing message {}", message);

        // Missing ResponseQueueUrl?

        MessageContent requestMessage = MessageContent.fromMessage(message);

        if (amazonSQSResponder.isResponseMessageRequested(requestMessage)) {
            LOGGER.debug("Responding with {}", message.getBody());
            amazonSQSResponder.sendResponseMessage(requestMessage, new MessageContent(message.getBody()));
        }

        amazonSQS.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(properties.getUrl())
                .withReceiptHandle(message.getReceiptHandle()));
    }
}
