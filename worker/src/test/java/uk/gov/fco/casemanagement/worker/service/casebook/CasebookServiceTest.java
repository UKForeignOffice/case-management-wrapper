package uk.gov.fco.casemanagement.worker.service.casebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.config.CasebookProperties;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.*;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CasebookServiceTest {

    private CasebookService casebookService;

    @Mock
    private DocumentUploadService documentUploadService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CasebookProperties properties;

    @Mock
    private Map<String, FeeService> feeServices;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        initMocks(this);
        casebookService = new CasebookService(documentUploadService, restTemplate,
                properties, objectMapper, feeServices);
    }

    @Test
    public void shouldSendCaseToCasebookWithCorrectHeaders() throws Exception {
        CreateCaseResponse response = new CreateCaseResponse();
        response.setApplicationReference("reference");

        when(restTemplate.exchange(anyString(), any(), any(), eq(CreateCaseResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(properties.getKey()).thenReturn("key");

        casebookService.createCase(Instant.ofEpochMilli(0), new Form(emptyList()));

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(), httpEntityArgumentCaptor.capture(), eq(CreateCaseResponse.class));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();

        assertThat(httpEntity, notNullValue());

        HttpHeaders headers = httpEntity.getHeaders();

        assertThat(headers, notNullValue());
        assertThat(headers.getAccept(), equalTo(ImmutableList.of(MediaType.APPLICATION_JSON)));
        assertThat(headers.getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
        assertThat(headers.get("hash"), equalTo(ImmutableList.of("052CA121D027B2C3B29FB4F87EBBDFF26B8AC1F27530DDAE3837A4776EA18C42775988064E5114E0B68F659EBACE6168E42B0ABEE5D4A42AF8E5D8C2E6B4BEC6")));
    }

    @Test
    public void shouldSendCaseToCasebook() throws Exception {
        final String post = "Beirut";
        final String caseType = "Consular Marriage";
        final String firstName = "Max";
        final String lastName = "Stewart";

        Form form = new FormBuilder()
                .withMetadata("post", post)
                .withMetadata("caseType", caseType)
                .withQuestion("firstName", firstName)
                .withQuestion("lastName", lastName)
                .build();

        CreateCaseResponse response = new CreateCaseResponse();
        response.setApplicationReference("reference");

        when(restTemplate.exchange(anyString(), any(), any(), eq(CreateCaseResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(properties.getKey()).thenReturn("key");

        casebookService.createCase(Instant.ofEpochMilli(0), form);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(), httpEntityArgumentCaptor.capture(), eq(CreateCaseResponse.class));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();

        assertThat(httpEntity, notNullValue());

        String body = (String) httpEntity.getBody();

        assertThat(body, startsWith("{\"notarialApplication\":"));

        body = body.substring("{\"notarialApplication\":".length(), body.length() - 1);

        NotarialApplication notarialApplication = objectMapper.readValue(body, NotarialApplication.class);

        assertThat(notarialApplication, notNullValue());
        assertThat(notarialApplication.getTimestamp(), equalTo(0L));

        Applicant applicant = notarialApplication.getApplicant();

        assertThat(applicant, notNullValue());
        assertThat(applicant.getSurname(), equalTo(lastName));
        assertThat(applicant.getForenames(), equalTo(firstName));

        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getPost(), equalTo(post));
        assertThat(application.getCaseType(), equalTo(caseType));
    }

    @Test
    public void shouldSerialiseFeeServices() throws Exception {
        final String post = "Beirut";
        final String caseType = "Consular Marriage";
        final String firstName = "Max";
        final String lastName = "Stewart";

        Form form = new FormBuilder()
                .withMetadata("post", post)
                .withMetadata("caseType", caseType)
                .withFees(new FeesBuilder("ref").withFeeDetail("feeService", BigDecimal.ONE).build())
                .withQuestion("firstName", firstName)
                .withQuestion("lastName", lastName)
                .build();

        CreateCaseResponse response = new CreateCaseResponse();
        response.setApplicationReference("reference");

        when(feeServices.get("feeService")).thenReturn(new FeeServiceBuilder()
                .withField("franceMarriageFullNameAtBirth")
                .build());

        when(restTemplate.exchange(anyString(), any(), any(), eq(CreateCaseResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(properties.getKey()).thenReturn("key");

        casebookService.createCase(Instant.ofEpochMilli(0), form);

        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(), httpEntityArgumentCaptor.capture(), eq(CreateCaseResponse.class));

        HttpEntity httpEntity = httpEntityArgumentCaptor.getValue();

        assertThat(httpEntity, notNullValue());

        String body = (String) httpEntity.getBody();

        assertThat(body.indexOf("\"type\":"), is(-1));
    }
}
