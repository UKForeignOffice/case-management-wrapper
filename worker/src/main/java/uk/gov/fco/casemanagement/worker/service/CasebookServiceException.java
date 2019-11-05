package uk.gov.fco.casemanagement.worker.service;

public class CasebookServiceException extends Exception {

    public CasebookServiceException(String message) {
        super(message);
    }

    public CasebookServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
