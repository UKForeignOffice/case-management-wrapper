package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

@ToString
public class Form {

    private String id;

    private String name;

    private Fees fees;

    private List<Question> questions = new ArrayList<>();

    @JsonCreator
    public Form(@JsonProperty("questions") @NonNull List<Question> questions) {
        this.questions.addAll(questions);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> metadata = new HashMap<>();

    public Fees getFees() {
        return fees;
    }

    public void setFees(Fees fees) {
        this.fees = fees;
    }

    public List<Question> getQuestions() {
        return unmodifiableList(questions);
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata.clear();
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
    }

    public Map<String, String> getMetadata() {
        return unmodifiableMap(metadata);
    }

    @JsonIgnore
    public Map<String, Object> getAnswers() {
        Map<String, Object> answers = new HashMap<>();
        questions.forEach(question -> {
            question.getFields().forEach(field -> {
                answers.put(field.getId(), field.getAnswer());
            });
        });
        answers.putAll(metadata);
        return answers;
    }
}
