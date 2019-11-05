package uk.gov.fco.casemanagement.worker.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CasebookConfig {

    private CasebookProperties properties;

    @Autowired
    public CasebookConfig(@NonNull CasebookProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
