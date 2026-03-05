package com.avega.test.service.transaction;

import com.avega.domain.transaction.*;
import com.avega.repo.transaction.TransactionsRepository;
import com.avega.service.audit.AuditService;
import com.avega.service.rules.TaxRuleEngine;
import com.avega.service.transaction.*;
import com.avega.utils.dto.transaction.TransactionRequest;
import com.avega.utils.dto.transaction.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private TransactionValidationService validationService;

    @Mock
    private TaxCalculationService calculationService;

    @Mock
    private TaxRuleEngine ruleEngine;

    @Mock
    private ExceptionService exceptionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequest validRequest;
    private TransactionRequest invalidRequest;
    private ValidationResult validResult;
    private ValidationResult invalidResult;

    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest();
        validRequest.setTransactionId("txn123");
        validRequest.setCustomerId("cust123");
        validRequest.setAmount(new BigDecimal("1000.00"));
        validRequest.setTaxRate(new BigDecimal("0.10"));
        validRequest.setReportedTax(new BigDecimal("100.00"));
        validRequest.setTransactionType(TransactionType.SALE);
        validRequest.setOriginalSaleAmount(new BigDecimal("1000.00"));

        invalidRequest = new TransactionRequest();
        invalidRequest.setTransactionId("txn456");
        invalidRequest.setCustomerId("cust456");
        // Missing required fields

        validResult = ValidationResult.builder()
                .status(true)
                .parsedDate(LocalDate.now())
                .build();

        invalidResult = ValidationResult.builder()
                .status(false)
                .errors(List.of("Invalid amount", "Invalid tax rate"))
                .build();
    }

    @Test
    void testUploadTransactions_ValidRequest() {
        // Given
        when(validationService.validateTransaction(validRequest)).thenReturn(validResult);
        when(ruleEngine.evaluate(any(Transactions.class))).thenReturn(List.of());

        // When
        transactionService.uploadTransactions(List.of(validRequest));

        // Then
        verify(validationService, times(1)).validateTransaction(validRequest);
        verify(calculationService, times(1)).calculateTax(any(Transactions.class));
        verify(ruleEngine, times(1)).evaluate(any(Transactions.class));
        verify(exceptionService, never()).logException(any(), any(), any(), any(), any());
        verify(auditService, times(1)).logEvent(eq(AuditEventType.INGESTION), eq("txn123"), any(Transactions.class));
        verify(transactionsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadTransactions_InvalidRequest() {
        // Given
        when(validationService.validateTransaction(invalidRequest)).thenReturn(invalidResult);

        // When
        transactionService.uploadTransactions(List.of(invalidRequest));

        // Then
        verify(validationService, times(1)).validateTransaction(invalidRequest);
        verify(calculationService, never()).calculateTax(any(Transactions.class));
        verify(ruleEngine, never()).evaluate(any(Transactions.class));
        verify(exceptionService, times(1)).logException(
                "txn456", "cust456", "VALIDATION_ERROR", Severity.HIGH, "Invalid amount, Invalid tax rate"
        );
        verify(auditService, times(1)).logEvent(eq(AuditEventType.INGESTION), eq("txn456"), any(Transactions.class));
        verify(transactionsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadTransactions_MixedRequests() {
        // Given
        when(validationService.validateTransaction(validRequest)).thenReturn(validResult);
        when(validationService.validateTransaction(invalidRequest)).thenReturn(invalidResult);
        when(ruleEngine.evaluate(any(Transactions.class))).thenReturn(List.of("Rule violation"));

        // When
        transactionService.uploadTransactions(List.of(validRequest, invalidRequest));

        // Then
        verify(validationService, times(2)).validateTransaction(any(TransactionRequest.class));
        verify(calculationService, times(1)).calculateTax(any(Transactions.class)); // Only for valid
        verify(ruleEngine, times(1)).evaluate(any(Transactions.class)); // Only for valid
        verify(exceptionService, times(1)).logException(any(), any(), any(), any(), any()); // Only for invalid
        verify(auditService, times(2)).logEvent(eq(AuditEventType.INGESTION), any(), any(Transactions.class));
        verify(transactionsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadTransactions_ValidRequestWithRuleViolations() {
        // Given
        when(validationService.validateTransaction(validRequest)).thenReturn(validResult);
        when(ruleEngine.evaluate(any(Transactions.class))).thenReturn(List.of("GST Slab Violation"));

        // When
        transactionService.uploadTransactions(List.of(validRequest));

        // Then
        verify(ruleEngine, times(1)).evaluate(any(Transactions.class));
        // Verify that ruleViolations is set in the entity
        ArgumentCaptor<List<Transactions>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionsRepository, times(1)).saveAll(captor.capture());
        List<Transactions> savedTransactions = captor.getValue();
        assertEquals(1, savedTransactions.size());
        assertEquals("GST Slab Violation", savedTransactions.get(0).getRuleViolations());
    }

    @Test
    void testUploadTransactions_NonCompliantTransaction() {
        // Given
        when(validationService.validateTransaction(validRequest)).thenReturn(validResult);
        // Mock calculationService to set NON_COMPLIANT
        doAnswer(invocation -> {
            Transactions entity = invocation.getArgument(0);
            entity.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
            return null;
        }).when(calculationService).calculateTax(any(Transactions.class));

        // When
        transactionService.uploadTransactions(List.of(validRequest));

        // Then
        verify(ruleEngine, never()).evaluate(any(Transactions.class)); // Because NON_COMPLIANT
    }

    @Test
    void testGetAllTransactions() {
        // Given
        List<Transactions> expectedTransactions = List.of(new Transactions());
        when(transactionsRepository.findAll()).thenReturn(expectedTransactions);

        // When
        List<Transactions> result = transactionService.getAllTransactions();

        // Then
        assertEquals(expectedTransactions, result);
        verify(transactionsRepository, times(1)).findAll();
    }

    @Test
    void testUploadTransactions_EmptyList() {
        // When
        transactionService.uploadTransactions(List.of());

        // Then
        verify(validationService, never()).validateTransaction(any());
        verify(calculationService, never()).calculateTax(any());
        verify(ruleEngine, never()).evaluate(any());
        verify(exceptionService, never()).logException(any(), any(), any(), any(), any());
        verify(auditService, never()).logEvent(any(), any(), any());
        verify(transactionsRepository, times(1)).saveAll(anyList()); // Empty list
    }
}
