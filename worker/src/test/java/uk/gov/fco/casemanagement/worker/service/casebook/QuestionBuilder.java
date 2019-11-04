package uk.gov.fco.casemanagement.worker.service.casebook;

import uk.gov.fco.casemanagement.common.domain.Field;
import uk.gov.fco.casemanagement.common.domain.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class QuestionBuilder {

    private String question;

    private List<Field> fields = new ArrayList<>();

    QuestionBuilder withQuestion(String question) {
        this.question = question;
        return this;
    }

    QuestionBuilder withField(String name, String property, String answer) {
        return withField(name, property, "text", answer);
    }

    QuestionBuilder withField(String name, String property, String type, String answer) {
        Field field = new Field(property, type, name);
        field.setAnswer(answer);
        fields.add(field);
        return this;
    }

    Question build() {
        Question question = new Question(UUID.randomUUID().toString());
        question.setQuestion(this.question);
        question.setFields(fields);
        return question;
    }
}
