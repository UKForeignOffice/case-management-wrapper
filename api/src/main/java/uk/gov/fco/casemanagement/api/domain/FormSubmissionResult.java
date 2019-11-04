package uk.gov.fco.casemanagement.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
        name = "FormSubmissionResult",
        description = "POJO that represents the result of submitting aa form."
)
public class FormSubmissionResult {

    @Schema(required = true)
    private String reference;

    public FormSubmissionResult(@NonNull String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
