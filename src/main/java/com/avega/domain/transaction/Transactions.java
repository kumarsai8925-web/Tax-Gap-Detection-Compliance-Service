package com.avega.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Transactions")
public class Transactions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String transactionId;
	private LocalDate transactionDate;
	private String customerId;

	private BigDecimal amount;
	private BigDecimal taxRate;
	private BigDecimal reportedTax;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	private String validationStatus; 

	@Column(length = 1000)
	private String failureReasons; 
	
	private BigDecimal expectedTax;
	private BigDecimal taxGap;
	
	@Enumerated(EnumType.STRING)
	private ComplianceStatus complianceStatus;

	private BigDecimal originalSaleAmount;

	@Column(length = 2000)
	private String ruleViolations;

}
