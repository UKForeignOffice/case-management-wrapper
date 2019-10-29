package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import static com.google.common.base.Preconditions.checkNotNull;

public class Field {

    private String property;

    private String type;

    private LocalisedString name;

    private String answer;

    @JsonCreator
    public Field(String property, String type, LocalisedString name) {
        this.property = checkNotNull(property);
        this.type = checkNotNull(type);
        this.name = checkNotNull(name);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getProperty() {
        return property;
    }

    public String getType() {
        return type;
    }

    public LocalisedString getName() {
        return name;
    }

    public String getAnswer() {
        return answer;
    }
}
