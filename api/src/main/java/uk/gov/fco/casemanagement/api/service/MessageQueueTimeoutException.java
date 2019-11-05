package uk.gov.fco.casemanagement.api.service;

public class MessageQueueTimeoutException extends Exception {

    public MessageQueueTimeoutException(Throwable cause) {
        super(cause);
    }
}
