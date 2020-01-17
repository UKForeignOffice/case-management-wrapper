package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

@ToString
@Schema(
        name = "Form",
        description = "POJO that represents a form."
)
public class Form {

    @Schema(name = "id", example = "thailand")
    private String id;

    @Schema(name = "name", example = "Prove your eligibility, Thailand")
    private String name;

    private Fees fees;

    private List<Question> questions = new ArrayList<>();

    public Map<String, String> metadata = new HashMap<>();

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
                if (field.getAnswer() != null) {
                    answers.put(field.getId(), field.getAnswer());
                }
            });
        });
        metadata.forEach((key, value) -> answers.put(key.toLowerCase(), value));
        return answers;
    }
}
