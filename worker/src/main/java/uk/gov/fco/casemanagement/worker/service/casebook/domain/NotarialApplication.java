package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

@ToString(onlyExplicitlyIncluded = true)
public class NotarialApplication {

    @ToString.Include
    @JsonSerialize(using = InstantSerializer.class)
    private Instant timestamp;

    @ToString.Include
    private Applicant applicant;

    private Application application;

    public NotarialApplication(@NonNull Applicant applicant,
                               @NonNull Application application) {
        this.applicant = applicant;
        this.application = application;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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
