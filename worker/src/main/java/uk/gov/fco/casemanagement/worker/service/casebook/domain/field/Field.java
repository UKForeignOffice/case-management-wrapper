package uk.gov.fco.casemanagement.worker.service.casebook.domain.field;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanField.class, name = "boolean"),
        @JsonSubTypes.Type(value = StringField.class, name = "string"),
        @JsonSubTypes.Type(value = DateField.class, name = "date"),
})
public abstract class Field<T> {

    private String fieldName;

    private String fieldListName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldListName() {
        return fieldListName;
    }

    public void setFieldListName(String fieldListName) {
        this.fieldListName = fieldListName;
    }

    public abstract T getValue();

    public abstract void setValue(String value);
}
