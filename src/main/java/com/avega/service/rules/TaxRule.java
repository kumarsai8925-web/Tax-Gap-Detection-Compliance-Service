package com.avega.service.rules;

import com.avega.domain.transaction.Transactions;

import com.avega.utils.dto.rules.RuleResult;
import tools.jackson.databind.JsonNode;

public interface TaxRule {

    String getRuleCode();

    RuleResult evaluate(Transactions transaction, JsonNode config);
}
