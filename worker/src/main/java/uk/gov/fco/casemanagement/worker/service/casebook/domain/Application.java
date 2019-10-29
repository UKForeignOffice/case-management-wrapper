package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private String post;
    private String casetype;
    private String summary;
    private String description;
    private String customerInsightConsent;
    private String reasonForBeingOverseas;
    private String marriageCategory;
    private List<Attachment> attachments = new ArrayList<>();
}
