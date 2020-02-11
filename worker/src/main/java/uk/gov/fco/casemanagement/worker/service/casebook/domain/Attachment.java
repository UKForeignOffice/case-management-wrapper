package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Value;

@Value
public class Attachment {

    private String fileName;

    private String fileData;

    private String documentType;
}
