package uk.gov.fco.casemanagement.api.service;

public class MessageQueueTimeoutException extends Throwable {

    public MessageQueueTimeoutException(Throwable cause) {
        super(cause);
    }
}
