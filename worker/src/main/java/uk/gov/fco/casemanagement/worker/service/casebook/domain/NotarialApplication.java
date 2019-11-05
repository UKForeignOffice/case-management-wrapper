package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

@ToString(onlyExplicitlyIncluded = true)
public class NotarialApplication {

    @ToString.Include
    @JsonSerialize(using = DefaultInstantSerialiser.class)
    @JsonDeserialize(using = DefaultInstantDeserialiser.class)
    private Instant timestamp;

    @ToString.Include
    private Applicant applicant;

    private Application application;

    @JsonCreator
    public NotarialApplication(@JsonProperty("applicant") @NonNull Applicant applicant,
                               @JsonProperty("application") @NonNull Application application) {
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
