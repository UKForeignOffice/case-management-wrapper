package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;

@ToString
public class Application {

    private List<Fee> fees = new ArrayList<>();

    private List<Question> questions;

    @JsonCreator
    public Application(List<Question> questions) {
        this.questions = checkNotNull(questions);
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
}
