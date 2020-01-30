package uk.gov.fco.casemanagement.worker.service.casebook;

import uk.gov.fco.casemanagement.common.domain.FeeDetail;
import uk.gov.fco.casemanagement.common.domain.Fees;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class FeesBuilder {

    private String reference;

    private BigDecimal total = BigDecimal.ZERO;

    private List<FeeDetail> details = new ArrayList<>();

    public FeesBuilder(String reference) {
        this.reference = reference;
    }

    FeesBuilder withFeeDetail(String description, BigDecimal amount) {
        details.add(new FeeDetail(description, amount));
        total = total.add(amount);
        return this;
    }

    Fees build() {
        Fees fees = new Fees(reference, total);
        fees.setDetails(details);
        return fees;
    }
}
