package uk.gov.fco.casemanagement.worker.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.CasebookService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void shouldSendApplicationToCasebook() throws Exception {
        final String formName = "name";

        Message message = new Message();
        message.setBody("{\"name\": \"" + formName + "\", \"questions\": []}");

        when(casebookService.createCase(any(), any())).thenReturn("reference");

        messageReceiver.processMessage(message);

        ArgumentCaptor<Form> formArgumentCaptor = ArgumentCaptor.forClass(Form.class);
        verify(casebookService).createCase(any(), formArgumentCaptor.capture());

        Form form = formArgumentCaptor.getValue();

        assertThat(form, notNullValue());
        assertThat(form.getName(), equalTo(formName));
    }

    @Test
    public void shouldRespondWithReferenceIfRequested() throws Exception {
        final String reference = "reference";

        Message message = new Message();
        message.setBody("{\"name\": \"name\", \"questions\": []}");

        when(casebookService.createCase(any(), any())).thenReturn(reference);

        messageReceiver.processMessage(message);

        ArgumentCaptor<MessageContent> responseCaptor = ArgumentCaptor.forClass(MessageContent.class);
        verify(amazonSQSResponder).sendResponseMessage(any(), responseCaptor.capture());

        MessageContent response = responseCaptor.getValue();

        assertThat(response, notNullValue());
        assertThat(response.getMessageBody(), equalTo(reference));
    }
}
