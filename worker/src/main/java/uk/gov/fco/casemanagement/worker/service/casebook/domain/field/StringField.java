package uk.gov.fco.casemanagement.worker.service.casebook.domain.field;

public class StringField extends Field<String> {

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
