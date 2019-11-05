package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

@ToString(onlyExplicitlyIncluded = true)
public class NotarialApplication {

    @ToString.Include
    private Long timestamp;

    @ToString.Include
    private Applicant applicant;

    private Application application;

    @JsonCreator
    public NotarialApplication(@JsonProperty("applicant") @NonNull Applicant applicant,
                               @JsonProperty("application") @NonNull Application application) {
        this.applicant = applicant;
        this.application = application;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Application getApplication() {
        return application;
    }
}
