package uk.gov.fco.casemanagement.worker.service.casebook;

import uk.gov.fco.casemanagement.worker.service.casebook.domain.FeeService;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.field.BooleanField;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.field.Field;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.field.StringField;

import java.util.ArrayList;
import java.util.List;

class FeeServiceBuilder {

    private String name;

    private List<Field> fields = new ArrayList<>();

    FeeServiceBuilder withName(String name) {
        this.name = name;
        return this;
    }

    FeeServiceBuilder withField(String fieldName) {
        Field field = new StringField();
        field.setFieldName(fieldName);
        fields.add(field);
        return this;
    }

    FeeServiceBuilder withBooleanField(String fieldName) {
        Field field = new BooleanField();
        field.setFieldName(fieldName);
        fields.add(field);
        return this;
    }

    FeeService build() {
        FeeService feeService = new FeeService();
        feeService.setName(name);
        feeService.setFields(fields);
        return feeService;
    }
}
