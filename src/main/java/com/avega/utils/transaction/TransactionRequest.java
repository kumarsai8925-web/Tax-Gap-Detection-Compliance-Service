package com.avega.utils.transaction;

import java.math.BigDecimal;

import com.avega.domain.transaction.TransactionType;

import lombok.Data;

@Data
public class TransactionRequest {

	  private String transactionId;
	    private String date; 
	    private String customerId;
	    private BigDecimal amount;
	    private BigDecimal taxRate;
	    private BigDecimal reportedTax;
	    private TransactionType transactionType;
		private BigDecimal originalSaleAmount;
}
