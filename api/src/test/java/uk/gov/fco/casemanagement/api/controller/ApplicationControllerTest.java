package uk.gov.fco.casemanagement.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.gov.fco.casemanagement.api.domain.FormSubmissionResult;
import uk.gov.fco.casemanagement.api.service.MessageQueueService;

import javax.servlet.AsyncListener;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ApplicationController controller;

    @Mock
    private MessageQueueService messageQueueService;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller)
                .build();
    }

    @Test
    public void shouldReturnReference() throws Exception {
        final String reference = "reference";

        when(messageQueueService.send(any())).thenReturn(reference);

        MvcResult result = mockMvc.perform(post("/applications")
                .content("{ \"questions\": [] }")
                .contentType("application/json"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference", equalTo(reference)));
    }

    @Test
    public void shouldReturnAcceptedOnTimeout() throws Exception {
        MvcResult result = mockMvc.perform(post("/applications")
                .content("{ \"questions\": [] }")
                .contentType("application/json"))
                .andExpect(request().asyncStarted())
                .andReturn();

        MockAsyncContext context = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : context.getListeners()) {
            listener.onTimeout(null);
        }

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isAccepted());
    }
}
