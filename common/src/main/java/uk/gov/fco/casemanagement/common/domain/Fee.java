package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class Fee {

    private String description;

    private BigDecimal amount;

    // TODO: receipt

    @JsonCreator
    public Fee(@JsonProperty("description") String description, @JsonProperty("amount") BigDecimal amount) {
        this.description = checkNotNull(description);
        this.amount = checkNotNull(amount);
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
