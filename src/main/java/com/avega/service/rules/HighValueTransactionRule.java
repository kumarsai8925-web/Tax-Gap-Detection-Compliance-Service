package com.avega.service.rules;

import com.avega.domain.transaction.Transactions;

import com.avega.utils.dto.rules.RuleResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
@Component
public class HighValueTransactionRule implements TaxRule {

    @Override
    public String getRuleCode() {
        return "HIGH_VALUE_TXN";
    }

    @Override
    public RuleResult evaluate(Transactions transaction, JsonNode config) {

        if (transaction.getAmount() == null)
            return RuleResult.builder().violated(false).build();

        BigDecimal threshold =
                config.get("threshold").decimalValue();

        if (transaction.getAmount().compareTo(threshold) > 0) {
            return RuleResult.builder()
                    .violated(true)
                    .message("High value transaction")
                    .build();
        }

        return RuleResult.builder().violated(false).build();
    }
}