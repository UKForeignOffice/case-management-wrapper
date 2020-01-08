package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class Application {

    private String post;

    private String caseType;

    private String summary;

    private String description;

    private String customerInsightConsent = "No";

    private String reasonForBeingOverseas;

    private String marriageCategory;

    private List<Attachment> attachments = new ArrayList<>();

    private List<FeeService> feeServices = new ArrayList<>();

    public void addAttachment(@NonNull Attachment attachment) {
        this.attachments.add(attachment);
    }
}
