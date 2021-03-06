package uk.gov.fco.casemanagement.worker.service.casebook;

import org.springframework.lang.NonNull;
import uk.gov.fco.casemanagement.common.domain.Fees;
import uk.gov.fco.casemanagement.common.domain.Field;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.common.domain.Question;

import java.util.*;

class FormBuilder {

    private Fees fees;

    private List<Question> questions = new ArrayList<>();

    private Map<String, String> metadata = new HashMap<>();

    FormBuilder withQuestion(String property, String answer) {

        Field field = new Field(property, "text", property);
        field.setAnswer(answer);

        Question question = new Question();
        question.addField(field);

        questions.add(question);

        return this;
    }

    FormBuilder withQuestion(@NonNull Question question) {
        questions.add(question);
        return this;
    }

    FormBuilder withMetadata(@NonNull String key, @NonNull String value) {
        metadata.put(key, value);
        return this;
    }

    FormBuilder withFees(Fees fees) {
        this.fees = fees;
        return this;
    }

    Form build() {
        Form form = new Form(questions);
        form.setMetadata(metadata);
        form.setFees(fees);
        return form;
    }
}
