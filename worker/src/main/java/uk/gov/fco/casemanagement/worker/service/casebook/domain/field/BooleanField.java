package uk.gov.fco.casemanagement.worker.service.casebook.domain.field;

public class BooleanField extends Field<Boolean> {

    private Boolean value;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = "true".equals(value) || "Yes".equals(value);
        }
    }
}
