package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.math.BigDecimal;

public class FeeDetail {

    private String description;

    private BigDecimal amount;

    @JsonCreator
    public FeeDetail(@JsonProperty("description") @NonNull String description,
                     @JsonProperty("amount") @NonNull BigDecimal amount) {
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
