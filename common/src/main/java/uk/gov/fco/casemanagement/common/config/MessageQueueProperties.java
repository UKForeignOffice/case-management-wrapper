package uk.gov.fco.casemanagement.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "message-queue")
@Data
public class MessageQueueProperties {

    private String queueUrl;

    private String endpoint;

    private String internalQueuePrefix;

    private int requestTimeout;
}
