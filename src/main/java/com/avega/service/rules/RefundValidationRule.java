package com.avega.service.rules;

import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.TransactionType;
import com.avega.domain.transaction.Transactions;
import com.avega.service.transaction.ExceptionService;
import com.avega.utils.dto.rules.RuleResult;
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

		if (transaction.getTransactionType() != TransactionType.REFUND) {
			return RuleResult.builder().violated(false).build();
		}

		if (transaction.getOriginalSaleAmount() == null) {
			return RuleResult.builder()
					.violated(true)
					.message("Original sale amount missing")
					.build();
		}

		if (transaction.getAmount()
				.compareTo(transaction.getOriginalSaleAmount()) > 0) {

			return RuleResult.builder()
					.violated(true)
					.message("Refund amount cannot exceed original sale amount")
					.build();
		}

		return RuleResult.builder().violated(false).build();
	}
}