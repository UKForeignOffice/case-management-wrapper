package uk.gov.fco.casemanagement.worker.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.CasebookService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageReceiverTest {

    private MessageReceiver messageReceiver;

    @Mock
    private AmazonSQS amazonSQS;

    @Mock
    private AmazonSQSResponder amazonSQSResponder;

    @Mock
    private MessageQueueProperties properties;

    @Mock
    private CasebookService casebookService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        initMocks(this);

        messageReceiver = new MessageReceiver(amazonSQS, amazonSQSResponder, properties, casebookService, objectMapper);

        when(amazonSQS.receiveMessage((ReceiveMessageRequest) any())).thenReturn(new ReceiveMessageResult());
    }

    @Test
    public void shouldPollForMessagesWithCorrectArguments() {

        final String queueURL = "http://example.org";
        final Integer maxNumberOfMessages = 1;

        when(properties.getQueueUrl()).thenReturn(queueURL);

        messageReceiver.receiveMessage();

        ArgumentCaptor<ReceiveMessageRequest> receiveMessageRequestArgumentCaptor = ArgumentCaptor.forClass(ReceiveMessageRequest.class);
        verify(amazonSQS).receiveMessage(receiveMessageRequestArgumentCaptor.capture());

        ReceiveMessageRequest receiveMessageRequest = receiveMessageRequestArgumentCaptor.getValue();

        assertThat(receiveMessageRequest, notNullValue());
        assertThat(receiveMessageRequest.getQueueUrl(), equalTo(queueURL));
        assertThat(receiveMessageRequest.getMaxNumberOfMessages(), equalTo(maxNumberOfMessages));
        assertThat(receiveMessageRequest.getAttributeNames(), containsInAnyOrder(QueueAttributeName.All.toString()));
        assertThat(receiveMessageRequest.getMessageAttributeNames(), containsInAnyOrder("ResponseQueueUrl"));
    }

    @Test
    public void shouldSendApplicationToCasebook() {

        final String formId = "id";

        Message message = new Message();
        message.setBody("{\"id\": \"" + formId + "\", \"questions\": []}");

        ReceiveMessageResult receiveMessageResult = new ReceiveMessageResult();
        receiveMessageResult.setMessages(ImmutableList.of(message));

        when(amazonSQS.receiveMessage((ReceiveMessageRequest) any())).thenReturn(receiveMessageResult);

        messageReceiver.receiveMessage();

        ArgumentCaptor<Form> formArgumentCaptor = ArgumentCaptor.forClass(Form.class);
        verify(casebookService).createCase(formArgumentCaptor.capture());

        Form form = formArgumentCaptor.getValue();

        assertThat(form, notNullValue());
        assertThat(form.getId(), equalTo(formId));
    }

    @Test
    public void shouldRespondWithReferenceIfRequested() {

        final String reference = "reference";

        Message message = new Message();
        message.setBody("{\"id\": \"id\", \"questions\": []}");

        ReceiveMessageResult receiveMessageResult = new ReceiveMessageResult();
        receiveMessageResult.setMessages(ImmutableList.of(message));

        when(amazonSQS.receiveMessage((ReceiveMessageRequest) any())).thenReturn(receiveMessageResult);
        when(casebookService.createCase(any())).thenReturn(reference);
        when(amazonSQSResponder.isResponseMessageRequested(any())).thenReturn(true);

        messageReceiver.receiveMessage();

        ArgumentCaptor<MessageContent> responseCaptor = ArgumentCaptor.forClass(MessageContent.class);
        verify(amazonSQSResponder).sendResponseMessage(any(), responseCaptor.capture());

        MessageContent response = responseCaptor.getValue();

        assertThat(response, notNullValue());
        assertThat(response.getMessageBody(), equalTo(reference));
    }

    @Test
    public void shouldDeleteMessage() {

        final String queueUrl = "http://example.org";
        final String receiptHandle = "receipt-handle";

        Message message = new Message();
        message.setReceiptHandle(receiptHandle);
        message.setBody("{\"id\": \"id\", \"questions\": []}");

        ReceiveMessageResult receiveMessageResult = new ReceiveMessageResult();
        receiveMessageResult.setMessages(ImmutableList.of(message));

        when(amazonSQS.receiveMessage((ReceiveMessageRequest) any())).thenReturn(receiveMessageResult);
        when(properties.getQueueUrl()).thenReturn(queueUrl);

        messageReceiver.receiveMessage();

        ArgumentCaptor<DeleteMessageRequest> deleteMessageRequestArgumentCaptor = ArgumentCaptor.forClass(DeleteMessageRequest.class);
        verify(amazonSQS).deleteMessage(deleteMessageRequestArgumentCaptor.capture());

        DeleteMessageRequest deleteMessageRequest = deleteMessageRequestArgumentCaptor.getValue();

        assertThat(deleteMessageRequest, notNullValue());
        assertThat(deleteMessageRequest.getQueueUrl(), equalTo(queueUrl));
        assertThat(deleteMessageRequest.getReceiptHandle(), equalTo(receiptHandle));
    }
}
