package com.avega.service.rules;

import com.avega.domain.transaction.Transactions;
import com.avega.utils.rules.RuleResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

@Component
public class GSTSlabViolationRule implements TaxRule{
    @Override
    public String getRuleCode() {
        return "GST_SLAB";
    }

    @Override
    public RuleResult evaluate(Transactions transaction, JsonNode config) {

        BigDecimal slabThreshold =
                config.get("slabThreshold").decimalValue();
        BigDecimal minTaxRate =
                config.get("minimumTaxRate").decimalValue();
        if (transaction.getAmount().compareTo(slabThreshold) > 0 &&
                transaction.getTaxRate().compareTo(minTaxRate) < 0) {

            return RuleResult.builder()
                    .violated(true)
                    .message("GST slab violation: tax rate below required slab")
                    .build();
        }

        return RuleResult.builder()
                .violated(false)
                .message(null)
                .build();
    }
}
