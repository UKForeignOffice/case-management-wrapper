package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

@ToString(onlyExplicitlyIncluded = true)
public class NotarialApplication {

    @ToString.Include
    private Instant timestamp;

    @ToString.Include
    private Applicant applicant;

    private Application application;

    public NotarialApplication(@NonNull Instant timestamp, @NonNull Applicant applicant,
                               @NonNull Application application) {
        this.timestamp = timestamp;
        this.applicant = applicant;
        this.application = application;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Application getApplication() {
        return application;
    }
}
