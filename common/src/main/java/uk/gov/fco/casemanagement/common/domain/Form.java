package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

@ToString
public class Form {

    private String id;

    private LocalisedString name;

    private List<Fee> fees = new ArrayList<>();

    private List<Question> questions;

    @JsonCreator
    public Form(@NonNull List<Question> questions) {
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public LocalisedString getName() {
        return name;
    }

    public List<Fee> getFees() {
        return unmodifiableList(fees);
    }

    public void setFees(List<Fee> fees) {
        this.fees.clear();
        if (fees != null) {
            this.fees.addAll(fees);
        }
    }

    public List<Question> getQuestions() {
        return unmodifiableList(questions);
    }

    @JsonIgnore
    public Map<String, String> getAnswers() {
        Map<String, String> answers = new HashMap<>();
        questions.forEach(question -> {
            question.getFields().forEach(field -> {
                answers.put(field.getProperty(), field.getAnswer());
            });
        });
        return answers;
    }
}
