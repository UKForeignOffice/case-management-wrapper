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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCasetype() {
        return casetype;
    }

    public void setCasetype(String casetype) {
        this.casetype = casetype;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerInsightConsent() {
        return customerInsightConsent;
    }

    public void setCustomerInsightConsent(String customerInsightConsent) {
        this.customerInsightConsent = customerInsightConsent;
    }

    public String getReasonForBeingOverseas() {
        return reasonForBeingOverseas;
    }

    public void setReasonForBeingOverseas(String reasonForBeingOverseas) {
        this.reasonForBeingOverseas = reasonForBeingOverseas;
    }

    public String getMarriageCategory() {
        return marriageCategory;
    }

    public void setMarriageCategory(String marriageCategory) {
        this.marriageCategory = marriageCategory;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
