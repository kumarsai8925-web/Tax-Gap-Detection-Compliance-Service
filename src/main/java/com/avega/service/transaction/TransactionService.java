package com.avega.service.transaction;

import java.util.ArrayList;
import java.util.List;

import com.avega.domain.transaction.ComplianceStatus;
import com.avega.repo.transaction.TransactionsRepository;
import com.avega.service.rules.TaxRuleEngine;
import org.springframework.stereotype.Service;

import com.avega.domain.transaction.Transactions;
import com.avega.utils.transaction.TransactionRequest;
import com.avega.utils.transaction.ValidationResult;

@Service
public class TransactionService {


	private TransactionsRepository transactionsRepository;
	private TransactionValidationService validationService;
    private TaxCalculationService calculationService;
	private TaxRuleEngine ruleEngine;
    
	public TransactionService(TransactionsRepository transactionsRepository,
							  TransactionValidationService validationService,
							  TaxCalculationService taxCalculationService,
							  TaxRuleEngine ruleEngine) {
		this.transactionsRepository = transactionsRepository;
		this.validationService = validationService;
		this.calculationService=taxCalculationService;
		this.ruleEngine=ruleEngine;
	}

	public void uploadTransactions(List<TransactionRequest> request) {
		List<Transactions> transactions = new ArrayList<>();

		for (TransactionRequest req : request) {
			ValidationResult validationResult = validationService.validateTransaction(req);

			Transactions entity = new Transactions();

			entity.setTransactionId(req.getTransactionId());
			entity.setCustomerId(req.getCustomerId());
			entity.setAmount(req.getAmount());
			entity.setTaxRate(req.getTaxRate());
			entity.setReportedTax(req.getReportedTax());
			entity.setTransactionType(req.getTransactionType());

			if (validationResult.isStatus()) {
				entity.setTransactionDate(validationResult.getParsedDate());
				entity.setValidationStatus("SUCCESS");
				entity.setFailureReasons(null);

				calculationService.calculateTax(entity);
				if (entity.getComplianceStatus() != ComplianceStatus.NON_COMPLIANT) {
					List<String> rulesViolationResult = ruleEngine.evaluate(entity);
					entity.setRuleViolations(String.join(", ", rulesViolationResult));
				}

			} else {
				entity.setValidationStatus("FAILURE");
				entity.setFailureReasons(String.join(", ", validationResult.getErrors()));
			}

			transactions.add(entity);

		}
		
		transactionsRepository.saveAll(transactions);
	}

	public List<Transactions> getAllTransactions(){
		return transactionsRepository.findAll();
	}

}
