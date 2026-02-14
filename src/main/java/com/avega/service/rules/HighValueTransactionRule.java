package com.avega.service.rules;

import com.avega.domain.transaction.Transactions;
import com.avega.utils.rules.RuleResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

@Component
public class HighValueTransactionRule implements TaxRule{
    @Override
    public String getRuleCode() {
        return "HIGH_VALUE_TXN";
    }

    @Override
    public RuleResult evaluate(Transactions transaction, JsonNode config) {
        BigDecimal threshold  = config.get("threshold").decimalValue();

        if(transaction.getAmount().compareTo(threshold) > 0){
            return RuleResult.builder()
                    .violated(true)
                    .message("Transaction amount exceeds the high value threshold of " + threshold)
                    .build();
        }

        return RuleResult.builder()
                .violated(false)
                .message(null)
                .build();
    }
}
