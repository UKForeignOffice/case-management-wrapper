package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FeeService {

    private String name;

    private List<Field> fields = new ArrayList<>();
}
