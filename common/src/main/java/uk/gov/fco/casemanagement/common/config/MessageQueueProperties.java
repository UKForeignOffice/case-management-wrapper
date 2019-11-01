package uk.gov.fco.casemanagement.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message-queue")
public class MessageQueueProperties {

    private String queueUrl;

    private String endpoint;

    private String internalQueuePrefix;

    private int requestTimeout;

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getInternalQueuePrefix() {
        return internalQueuePrefix;
    }

    public void setInternalQueuePrefix(String internalQueuePrefix) {
        this.internalQueuePrefix = internalQueuePrefix;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
