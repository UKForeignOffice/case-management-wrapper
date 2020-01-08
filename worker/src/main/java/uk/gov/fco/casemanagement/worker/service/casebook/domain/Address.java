package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;

@Data
public class Address {

    private String companyName;

    private String flatNumber;

    private String premises;

    private String houseNumber;

    private String street;

    private String district;

    private String town;

    private String region;

    private String postcode;

    private String country;
}
