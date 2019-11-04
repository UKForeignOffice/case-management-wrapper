package uk.gov.fco.casemanagement.worker.service.casebook;

import org.springframework.lang.NonNull;
import uk.gov.fco.casemanagement.common.domain.Field;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.common.domain.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class FormBuilder {

    private List<Question> questions = new ArrayList<>();

    FormBuilder withQuestion(String property, String answer) {

        Field field = new Field(property, "text", property);
        field.setAnswer(answer);

        Question question = new Question(UUID.randomUUID().toString());
        question.addField(field);

        questions.add(question);

        return this;
    }

    FormBuilder withQuestion(@NonNull Question question) {
        questions.add(question);
        return this;
    }

    Form build() {
        Form form = new Form(questions);
        return form;
    }
}
