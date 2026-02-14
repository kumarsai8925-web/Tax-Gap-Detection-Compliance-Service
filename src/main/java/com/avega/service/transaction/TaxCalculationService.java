package com.avega.service.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.avega.domain.transaction.ComplianceStatus;
import com.avega.domain.transaction.Transactions;

@Service
public class TaxCalculationService {
	
	private static final BigDecimal THRESHOLD=BigDecimal.ONE;
	
	public void calculateTax(Transactions transaction) {

		if(transaction.getReportedTax()==null )	{
			transaction.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
			return;
		}

		BigDecimal expectedTax = transaction.getAmount()
				.multiply(transaction.getTaxRate())
				.setScale(2, RoundingMode.HALF_UP);


		BigDecimal taxGap = expectedTax.subtract(transaction.getReportedTax())
		.setScale(2, RoundingMode.HALF_UP);
		
		transaction.setExpectedTax(expectedTax);
		transaction.setTaxGap(taxGap);
		if (taxGap.abs().compareTo(THRESHOLD) <= 0) {
		    transaction.setComplianceStatus(ComplianceStatus.COMPLIANT);
		} else if (taxGap.compareTo(BigDecimal.ZERO) > 0) {
		    transaction.setComplianceStatus(ComplianceStatus.UNDERPAID);
		} else {
		    transaction.setComplianceStatus(ComplianceStatus.OVERPAID);
		}

		
	}

}
