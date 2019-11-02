package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

public class Question {

    private String id;

    private LocalisedString category;

    private LocalisedString question;

    private List<Field> fields = new ArrayList<>();

    @JsonCreator
    public Question(@JsonProperty("id") String id) {
        this.id = checkNotNull(id);
    }

    public String getId() {
        return id;
    }

    public LocalisedString getCategory() {
        return category;
    }

    public void setCategory(LocalisedString category) {
        this.category = category;
    }

    public LocalisedString getQuestion() {
        return question;
    }

    public void setQuestion(LocalisedString question) {
        this.question = question;
    }

    public List<Field> getFields() {
        return unmodifiableList(fields);
    }

    public void setFields(List<Field> fields) {
        this.fields.clear();
        if (fields != null) {
            this.fields.addAll(fields);
        }
    }
}
