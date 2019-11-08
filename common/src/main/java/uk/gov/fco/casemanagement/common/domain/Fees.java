package uk.gov.fco.casemanagement.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Fees {

    private String paymentReference;

    private BigDecimal total;

    private List<FeeDetail> details = new ArrayList<>();

    @JsonCreator
    public Fees(@JsonProperty("paymentReference") @NonNull String paymentReference,
                @JsonProperty("total") @NonNull BigDecimal total) {
        this.paymentReference = paymentReference;
        this.total = total;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<FeeDetail> getDetails() {
        return unmodifiableList(details);
    }

    public void setDetails(List<FeeDetail> details) {
        this.details.clear();
        if (details != null) {
            this.details.addAll(details);
        }
    }
}
