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

    QuestionBuilder withField(String title, String key, String answer) {
        return withField(title, key, "text", answer);
    }

    QuestionBuilder withField(String title, String key, String type, String answer) {
        Field field = new Field(key, type, title);
        field.setAnswer(answer);
        fields.add(field);
        return this;
    }

    Question build() {
        Question question = new Question();
        question.setQuestion(this.question);
        question.setFields(fields);
        return question;
    }
}
