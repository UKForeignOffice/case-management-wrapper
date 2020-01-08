package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class NotarialApplication {

    @ToString.Include
    private Long timestamp;

    @ToString.Include
    private Applicant applicant = new Applicant();

    private Application application = new Application();
}
