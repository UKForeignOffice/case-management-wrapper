package uk.gov.fco.casemanagement.worker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "casebook")
@Data
public class CasebookProperties {

    private String url;

    private String key;

    private String clientCertificate;

    private String clientKey;
}
