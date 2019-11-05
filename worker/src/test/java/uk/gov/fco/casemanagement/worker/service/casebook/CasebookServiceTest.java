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
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Applicant;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Application;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.CreateCaseResponse;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import java.time.Instant;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
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
        assertThat(headers.get("hmac"), equalTo(ImmutableList.of("d79fee361340e9322042ff54682ee0c4a4db22fa68059b8bd7d0987e1817c5e1c17e60e597b0610bf17ad2c9037defe80b48ff08255ae42b9566ea14395508b4")));
    }

    @Test
    public void shouldSendCaseToCasebook() throws Exception {
        final String post = "Beirut";
        final String caseType = "Consular Marriage";
        final String summary = "Certificate of No Impediment (Fee 10 & Fee 11ii)";
        final String firstName = "Max";
        final String lastName = "Stewart";

        Form form = new FormBuilder()
                .withMetadata("post", post)
                .withMetadata("caseType", caseType)
                .withMetadata("summary", summary)
                .withQuestion("firstName", firstName)
                .withQuestion("lastName", lastName)
                .build();

        CreateCaseResponse response = new CreateCaseResponse();
        response.setApplicationReference("reference");

        when(restTemplate.exchange(anyString(), any(), any(), eq(CreateCaseResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(properties.getKey()).thenReturn("key");

        casebookService.createCase(Instant.MIN, form);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(), httpEntityArgumentCaptor.capture(), eq(CreateCaseResponse.class));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();

        assertThat(httpEntity, notNullValue());

        NotarialApplication notarialApplication = objectMapper.readValue((String) httpEntity.getBody(), NotarialApplication.class);

        assertThat(notarialApplication, notNullValue());
        assertThat(notarialApplication.getTimestamp(), equalTo(Instant.MIN));

        Applicant applicant = notarialApplication.getApplicant();

        assertThat(applicant, notNullValue());
        assertThat(applicant.getSurname(), equalTo(lastName));
        assertThat(applicant.getForenames(), equalTo(firstName));

        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getPost(), equalTo(post));
        assertThat(application.getSummary(), equalTo(summary));
        assertThat(application.getCasetype(), equalTo(caseType));
    }
}
