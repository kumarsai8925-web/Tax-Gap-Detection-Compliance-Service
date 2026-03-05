package com.avega.service.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.avega.domain.transaction.AuditEventType;
import com.avega.domain.transaction.Severity;
import com.avega.service.audit.AuditService;
import org.springframework.stereotype.Service;

import com.avega.domain.transaction.ComplianceStatus;
import com.avega.domain.transaction.Transactions;

@Service
public class TaxCalculationService {

	private static final BigDecimal THRESHOLD = BigDecimal.ONE;
	private static final BigDecimal HIGH_GAP = new BigDecimal("10000");
	private static final BigDecimal MEDIUM_GAP = new BigDecimal("1000");

	private final ExceptionService exceptionService;
	private final AuditService auditService;

	public TaxCalculationService(ExceptionService exceptionService, AuditService auditService) {
		this.exceptionService = exceptionService;
        this.auditService = auditService;
    }

	public void calculateTax(Transactions transaction) {

		if (transaction.getAmount() == null
				|| transaction.getTaxRate() == null
				|| transaction.getReportedTax() == null) {

			transaction.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);

			exceptionService.logException(
					transaction.getTransactionId(),
					transaction.getCustomerId(),
					"TAX_CALCULATION",
					Severity.HIGH,
					"Mandatory fields missing"
			);
			return;
		}

		BigDecimal expectedTax = transaction.getAmount()
				.multiply(transaction.getTaxRate())
				.setScale(2, RoundingMode.HALF_UP);

		BigDecimal taxGap = expectedTax
				.subtract(transaction.getReportedTax())
				.setScale(2, RoundingMode.HALF_UP);

		transaction.setExpectedTax(expectedTax);
		transaction.setTaxGap(taxGap);

		if (taxGap.abs().compareTo(THRESHOLD) <= 0) {
			transaction.setComplianceStatus(ComplianceStatus.COMPLIANT);
			return;
		}

		Severity severity = determineSeverity(taxGap.abs());

		if (taxGap.compareTo(BigDecimal.ZERO) > 0) {
			transaction.setComplianceStatus(ComplianceStatus.UNDERPAID);
		} else {
			transaction.setComplianceStatus(ComplianceStatus.OVERPAID);
		}

		Map<String, Object> detail = new HashMap<>();
		detail.put("amount", transaction.getAmount());
		detail.put("taxRate", transaction.getTaxRate());
		detail.put("expectedTax", expectedTax);
		detail.put("reportedTax", transaction.getReportedTax());
		detail.put("taxGap", taxGap);
		detail.put("complianceStatus", transaction.getComplianceStatus());

		auditService.logEvent(
				AuditEventType.TAX_COMPUTATION,
				transaction.getTransactionId(),
				detail
		);

		exceptionService.logException(
				transaction.getTransactionId(),
				transaction.getCustomerId(),
				"TAX_GAP",
				severity,
				"Tax gap: " + taxGap
		);
	}

	private Severity determineSeverity(BigDecimal gap) {
		if (gap.compareTo(HIGH_GAP) > 0) return Severity.HIGH;
		if (gap.compareTo(MEDIUM_GAP) > 0) return Severity.MEDIUM;
		return Severity.LOW;
	}
}
