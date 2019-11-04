package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public class Field {

    private String id;

    private String type;

    private String title;

    private String answer;

    @JsonCreator
    public Field(@JsonProperty("id") @NonNull String id,
                 @JsonProperty("type") @NonNull String type,
                 @JsonProperty("title") @NonNull String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }
}
