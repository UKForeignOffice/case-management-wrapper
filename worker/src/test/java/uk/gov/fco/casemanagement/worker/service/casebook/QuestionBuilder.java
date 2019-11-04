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

    QuestionBuilder withField(String title, String id, String answer) {
        return withField(title, id, "text", answer);
    }

    QuestionBuilder withField(String title, String id, String type, String answer) {
        Field field = new Field(id, type, title);
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
