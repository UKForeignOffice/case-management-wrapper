package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;

@Data
public class Field {

    private String fieldName;

    private String fieldListName;

    private String value;
}
