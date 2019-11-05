package uk.gov.fco.casemanagement.worker.handler;

public class MessageReceiverException extends RuntimeException {

    public MessageReceiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
