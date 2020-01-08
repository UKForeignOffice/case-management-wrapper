package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class Applicant {

    @ToString.Include
    private String reference;

    private String forenames;

    private String surname;

    private String title;

    private String dateOfBirth;

    private String email;

    private String primaryTelephone;

    private String mobileTelephone;

    private String eveningTelephone;

    private String language;

    private String ethnicity;

    private String nationality;

    private String secondNationality;

    private String cityOfBirth;

    private String countryOfBirth;

    private Address address = new Address();
}
