package com.avega.service.transaction;

import java.util.ArrayList;
import java.util.List;

import com.avega.domain.transaction.*;
import com.avega.repo.transaction.TransactionsRepository;
import com.avega.service.audit.AuditService;
import com.avega.service.rules.TaxRuleEngine;
import com.avega.utils.dto.transaction.TransactionRequest;
import org.springframework.stereotype.Service;


import com.avega.utils.dto.transaction.ValidationResult;

@Service
public class TransactionService {

	private final TransactionsRepository transactionsRepository;
	private final TransactionValidationService validationService;
	private final TaxCalculationService calculationService;
	private final TaxRuleEngine ruleEngine;
	private final ExceptionService exceptionService;
	private final AuditService auditService;

	public TransactionService(
            TransactionsRepository transactionsRepository,
            TransactionValidationService validationService,
            TaxCalculationService calculationService,
            TaxRuleEngine ruleEngine,
            ExceptionService exceptionService, AuditService auditService) {

		this.transactionsRepository = transactionsRepository;
		this.validationService = validationService;
		this.calculationService = calculationService;
		this.ruleEngine = ruleEngine;
		this.exceptionService = exceptionService;
        this.auditService = auditService;
    }

	public void uploadTransactions(List<TransactionRequest> request) {

		List<Transactions> transactions = new ArrayList<>();

		for (TransactionRequest req : request) {

			ValidationResult validationResult =
					validationService.validateTransaction(req);

			Transactions entity = new Transactions();

			entity.setTransactionId(req.getTransactionId());
			entity.setCustomerId(req.getCustomerId());
			entity.setAmount(req.getAmount());
			entity.setTaxRate(req.getTaxRate());
			entity.setReportedTax(req.getReportedTax());
			entity.setTransactionType(req.getTransactionType());
			entity.setOriginalSaleAmount(req.getOriginalSaleAmount());

			if (validationResult.isStatus()) {

				entity.setTransactionDate(validationResult.getParsedDate());
				entity.setValidationStatus("SUCCESS");

				calculationService.calculateTax(entity);

				if (entity.getComplianceStatus() != ComplianceStatus.NON_COMPLIANT) {
					List<String> violations = ruleEngine.evaluate(entity);
					entity.setRuleViolations(String.join(", ", violations));
				}

			} else {

				entity.setValidationStatus("FAILURE");
				entity.setFailureReasons(
						String.join(", ", validationResult.getErrors())
				);

				exceptionService.logException(
						entity.getTransactionId(),
						entity.getCustomerId(),
						"VALIDATION_ERROR",
						Severity.HIGH,
						entity.getFailureReasons()
				);
			}

			auditService.logEvent(
					AuditEventType.INGESTION,
					entity.getTransactionId(),
					entity
			);
			transactions.add(entity);
		}

		transactionsRepository.saveAll(transactions);

	}

	public List<Transactions> getAllTransactions() {
		return transactionsRepository.findAll();
	}
}
