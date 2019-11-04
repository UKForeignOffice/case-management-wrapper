package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class Field {

    private String property;

    private String type;

    private String name;

    private String answer;

    @JsonCreator
    public Field(@JsonProperty("property") String property, @JsonProperty("type") String type,
                 @JsonProperty("name") String name) {
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

    public String getName() {
        return name;
    }

    public String getAnswer() {
        return answer;
    }
}
