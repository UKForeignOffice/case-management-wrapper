package uk.gov.fco.casemanagement.worker.service.casebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.config.CasebookProperties;
import uk.gov.fco.casemanagement.worker.service.CasebookServiceException;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.CreateCaseResponse;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.FeeService;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class CasebookService {

    private static final String HMAC_ALGORITHM = "HmacSHA512";

    private static final String HMAC_HEADER_NAME = "hash";

    private static final String SUBMIT_APPLICATION_PATH = "/jaxrs/notarial/submitApplication";

    private DocumentUploadService documentUploadService;

    private RestTemplate restTemplate;

    private CasebookProperties properties;

    private ObjectMapper objectMapper;

    private Map<String, FeeService> feeServices;

    @Autowired
    public CasebookService(@NonNull DocumentUploadService documentUploadService,
                           @NonNull RestTemplate restTemplate,
                           @NonNull CasebookProperties properties,
                           @NonNull ObjectMapper objectMapper,
                           @NonNull Map<String, FeeService> feeServices) {
        this.documentUploadService = documentUploadService;
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.feeServices = feeServices;
    }

    public String createCase(@NonNull Instant submittedAt, @NonNull Form form) throws CasebookServiceException {
        log.debug("Creating case from {}", form);

        NotarialApplication notarialApplication = new ApplicationConverter(this, documentUploadService)
                .convert(form);

        notarialApplication.setTimestamp(submittedAt.toEpochMilli());

        try {
            String requestBody = "{\"notarialApplication\":" + objectMapper.writeValueAsString(notarialApplication) + "}";
            String hmac = createHmac(requestBody, properties.getKey());

            log.debug("Sending request: {}", requestBody);
            log.debug("Sending request hmac: {}", hmac);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HMAC_HEADER_NAME, hmac);

            ResponseEntity<CreateCaseResponse> responseEntity = restTemplate.exchange(
                    properties.getUrl() + SUBMIT_APPLICATION_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    CreateCaseResponse.class);

            log.debug("Received response: {}", responseEntity);

            CreateCaseResponse response = responseEntity.getBody();

            if (response == null) {
                throw new CasebookServiceException("No response received from CASEBOOK");
            }
            return response.getApplicationReference();
        } catch (JsonProcessingException e) {
            throw new CasebookServiceException("Error formatting json request body", e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CasebookServiceException("Error creating HMAC hash", e);
        } catch (RestClientException e) {
            if (e instanceof HttpClientErrorException.UnprocessableEntity) {
                log.error("Validation error response from CASEBOOK, body = {}",
                        ((HttpClientErrorException.UnprocessableEntity) e).getResponseBodyAsString());
            }
            throw new CasebookServiceException("Error sending form to CASEBOOK", e);
        }
    }

    private String createHmac(String requestBody, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac digest = Mac.getInstance(HMAC_ALGORITHM);
        digest.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
        digest.update(requestBody.getBytes());
        return Hex.encodeHexString(digest.doFinal(), false);
    }

    public @NonNull List<FeeService> getFeeServices(List<String> names) {
        List<FeeService> relevantFeeServices = new ArrayList<>();
        for (String name : names) {
            FeeService feeService = feeServices.get(name);
            if (feeService != null) {
                relevantFeeServices.add(feeService);
            } else {
                log.info("No fee service found for name {}", name);
            }
        }
        return relevantFeeServices;
    }
}
