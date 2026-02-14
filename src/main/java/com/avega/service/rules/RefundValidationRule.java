package com.avega.service.rules;

import com.avega.domain.transaction.TransactionType;
import com.avega.domain.transaction.Transactions;
import com.avega.utils.rules.RuleResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
public class RefundValidationRule implements TaxRule {
	@Override
	public String getRuleCode() {
		return "REFUND_VALIDATION";
	}

	@Override
	public RuleResult evaluate(Transactions transaction, JsonNode config) {

		if (transaction.getTransactionType() == TransactionType.REFUND) {
			if (transaction.getOriginalSaleAmount() != null
					&& transaction.getAmount().compareTo(transaction.getOriginalSaleAmount()) > 0) {

				return RuleResult.builder().violated(true).message("Refund amount exceeds original sale amount")
						.build();
			} else {
				return RuleResult.builder().violated(true)
						.message("Original sale amount missing for refund transaction").build();
			}

		}

		return RuleResult.builder().violated(false).message(null).build();
	}
}
