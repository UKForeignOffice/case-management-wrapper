package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Field {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String id;

    private String type;

    private String title;

    private String answer;

    @JsonCreator
    public Field(@JsonProperty("id") @NonNull String id,
                 @JsonProperty("type") @NonNull String type,
                 @JsonProperty("title") @NonNull String title) {
        this.id = id.toLowerCase();
        this.type = type;
        this.title = title;
    }

    public void setAnswer(String answer) {
        if (isNotBlank(answer)) {
            this.answer = answer;
        } else {
            this.answer = null;
        }
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

    public Object getAnswer() {
        if (answer != null && "date".equals(type)) {
            try {
                return DATE_FORMAT.parse(answer);
            } catch (ParseException e) {
                throw new RuntimeException("Error parsing date", e);
            }
        }
        return answer;
    }
}
