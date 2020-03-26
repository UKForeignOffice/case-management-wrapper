package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Question {

    @Schema(example = "checkBeforeYouStart")
    private String category;

    @Schema(example = "Do you have a UK passport?")
    private String question;

    private List<Field> fields = new ArrayList<>();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
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

    public void addField(@NonNull Field field) {
        this.fields.add(field);
    }
}
