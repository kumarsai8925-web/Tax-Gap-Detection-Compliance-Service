package com.avega.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.avega.domain.transaction.TransactionType;
import com.avega.utils.transaction.TransactionRequest;
import com.avega.utils.transaction.ValidationResult;
import org.springframework.stereotype.Service;



@Service
public class TransactionValidationService {

	public ValidationResult validateTransaction(TransactionRequest  request) {
		List<String> errors = new ArrayList<>();

		LocalDate parsedDate = null;

		if (request.getTransactionId() == null)
			errors.add("transactionId is required");

		if (request.getCustomerId() == null)
			errors.add("customerId is required");

		if (request.getAmount() == null)
			errors.add("amount is required");

		if (request.getTransactionType() == null)
			errors.add("transactionType is required");

		if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			errors.add("amount must be greater than 0");
		}
		if (request.getTransactionType() == TransactionType.REFUND) {
		    if (request.getOriginalSaleAmount() == null) {
		        errors.add("originalSaleAmount is required for refund transactions");
		    } else if (request.getAmount().compareTo(request.getOriginalSaleAmount()) > 0) {
		        errors.add("refund amount cannot exceed original sale amount");
		    }
		}



		try {
			parsedDate = LocalDate.parse(request.getDate());
		} catch (DateTimeParseException e) {
			errors.add("invalid date format");
		}

		return ValidationResult.builder()
				.status(errors.isEmpty())
				.errors(errors)
				.parsedDate(parsedDate)
				.build();
	}
}
