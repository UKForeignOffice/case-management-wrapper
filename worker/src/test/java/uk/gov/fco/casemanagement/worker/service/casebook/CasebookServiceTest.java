package uk.gov.fco.casemanagement.worker.service.casebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.config.CasebookProperties;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.CreateCaseResponse;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import java.time.Instant;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CasebookServiceTest {

    private CasebookService casebookService;

    @Mock
    private DocumentUploadService documentUploadService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CasebookProperties properties;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        casebookService = new CasebookService(documentUploadService, restTemplate,
                properties, objectMapper);
    }

    @Test
    public void shouldSendCaseToCasebookWithCorrectHeaders() throws Exception {
        CreateCaseResponse response = new CreateCaseResponse();
        response.setApplicationReference("reference");

        when(restTemplate.exchange(anyString(), any(), any(), eq(CreateCaseResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(properties.getKey()).thenReturn("key");

        casebookService.createCase(Instant.MIN, new Form(emptyList()));

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(), httpEntityArgumentCaptor.capture(), eq(CreateCaseResponse.class));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();

        assertThat(httpEntity, notNullValue());

        HttpHeaders headers = httpEntity.getHeaders();

        assertThat(headers, notNullValue());
        assertThat(headers.getAccept(), equalTo(ImmutableList.of(MediaType.APPLICATION_JSON)));
        assertThat(headers.getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
        assertThat(headers.get("hmac"), equalTo(ImmutableList.of("f262065233311fea5f63ca7c4ade936369ae71e693840c3189681a34134ea4a61fd12e2f720b0d112ebe77834a1ff50524175ecf431db399dbbf45eb3b20b49c")));
    }
}
