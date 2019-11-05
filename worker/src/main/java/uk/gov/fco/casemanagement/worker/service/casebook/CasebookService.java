package uk.gov.fco.casemanagement.worker.service.casebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.config.CasebookProperties;
import uk.gov.fco.casemanagement.worker.service.CasebookServiceException;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.CreateCaseResponse;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
public class CasebookService {

    private static final String HMAC_ALGORITHM = "HmacSHA512";

    private static final String HMAC_HEADER_NAME = "hmac";

    private static final String SUBMIT_APPLICATION_PATH = "/jaxrs/notarial/submitApplication";

    private DocumentUploadService documentUploadService;

    private RestTemplate restTemplate;

    private CasebookProperties properties;

    private ObjectMapper objectMapper;

    @Autowired
    public CasebookService(@NonNull DocumentUploadService documentUploadService,
                           @NonNull RestTemplate restTemplate,
                           @NonNull CasebookProperties properties,
                           @NonNull ObjectMapper objectMapper) {
        this.documentUploadService = documentUploadService;
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String createCase(@NonNull Instant submittedAt, @NonNull Form form) throws CasebookServiceException {
        log.debug("Creating case from {}", form);

        NotarialApplication notarialApplication = new ApplicationConverter(documentUploadService)
                .convert(form);

        notarialApplication.setTimestamp(submittedAt);

        try {
            String requestBody = objectMapper.writeValueAsString(notarialApplication);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HMAC_HEADER_NAME, createHmac(requestBody, properties.getKey()));

            ResponseEntity<CreateCaseResponse> responseEntity = restTemplate.exchange(
                    properties.getUrl() + SUBMIT_APPLICATION_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    CreateCaseResponse.class);

            log.debug("Received response from casebook: {}", responseEntity);

            CreateCaseResponse response = responseEntity.getBody();

            if (response == null) {
                throw new CasebookServiceException("No response received from casebook");
            }
            return response.getApplicationReference();
        } catch (JsonProcessingException e) {
            throw new CasebookServiceException("Error formatting json request body", e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CasebookServiceException("Error creating HMAC hash", e);
        } catch (RestClientException e) {
            throw new CasebookServiceException("Error sending form to casebook", e);
        }
    }

    private String createHmac(String requestBody, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac digest = Mac.getInstance(HMAC_ALGORITHM);
        digest.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
        digest.update(requestBody.getBytes());
        return Hex.encodeHexString(digest.doFinal());
    }
}
