package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class Fee {

    private String description;

    private BigDecimal amount;

    private String currency;

    // TODO: receipt

    @JsonCreator
    public Fee(String description, BigDecimal amount, String currency) {
        this.description = checkNotNull(description);
        this.amount = checkNotNull(amount);
        this.currency = checkNotNull(currency);
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
