package uk.gov.fco.casemanagement.api.service;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.fco.casemanagement.common.config.MessageQueueProperties;
import uk.gov.fco.casemanagement.common.domain.Form;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageQueueServiceTest {

    private MessageQueueService messageQueueService;

    @Mock
    private AmazonSQSRequester amazonSQSRequester;

    @Mock
    private MessageQueueProperties properties;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        initMocks(this);
        messageQueueService = new MessageQueueService(amazonSQSRequester, properties, objectMapper);
    }

    @Test
    public void shouldSendForm() throws Exception {
        final String expectedReference = "ref";

        Message response = new Message();
        response.setBody(expectedReference);

        Form form = new Form(ImmutableList.of());

        when(amazonSQSRequester.sendMessageAndGetResponse(any(), anyInt(), any()))
                .thenReturn(response);

        String reference = messageQueueService.send(form);

        assertThat(reference, equalTo(expectedReference));
    }

    @Test(expected = MessageQueueTimeoutException.class)
    public void shouldThrowExceptionOnTimeout() throws Exception {
        Form form = new Form(ImmutableList.of());

        when(amazonSQSRequester.sendMessageAndGetResponse(any(), anyInt(), any()))
                .thenThrow(new TimeoutException());

        messageQueueService.send(form);
    }
}
