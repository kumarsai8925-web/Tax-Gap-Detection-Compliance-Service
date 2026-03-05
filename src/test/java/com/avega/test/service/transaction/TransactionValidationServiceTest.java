package com.avega.test.service.transaction;

import com.avega.domain.transaction.TransactionType;
import com.avega.service.transaction.TransactionValidationService;
import com.avega.utils.dto.transaction.TransactionRequest;
import com.avega.utils.dto.transaction.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionValidationServiceTest {

    private TransactionValidationService validationService;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        validationService = new TransactionValidationService();
        request = new TransactionRequest();
        request.setTransactionId("txn123");
        request.setCustomerId("cust123");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransactionType(TransactionType.SALE);
        request.setDate("2023-10-01");
    }

    @Test
    void testValidateTransaction_ValidRequest() {
        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertTrue(result.isStatus());
        assertTrue(result.getErrors().isEmpty());
        assertEquals(LocalDate.of(2023, 10, 1), result.getParsedDate());
    }

    @Test
    void testValidateTransaction_MissingTransactionId() {
        // Given
        request.setTransactionId(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("transactionId is required", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_MissingCustomerId() {
        // Given
        request.setCustomerId(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("customerId is required", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_MissingAmount() {
        // Given
        request.setAmount(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("amount is required", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_MissingTransactionType() {
        // Given
        request.setTransactionType(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("transactionType is required", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_AmountZero() {
        // Given
        request.setAmount(BigDecimal.ZERO);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("amount must be greater than 0", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_AmountNegative() {
        // Given
        request.setAmount(new BigDecimal("-100.00"));

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("amount must be greater than 0", result.getErrors().get(0));
    }

    @Test
    void testValidateTransaction_RefundMissingOriginalSaleAmount() {
        // Given
        request.setTransactionType(TransactionType.REFUND);
        request.setOriginalSaleAmount(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertTrue(result.getErrors().contains("originalSaleAmount is required for refund transactions"));
    }

    @Test
    void testValidateTransaction_RefundAmountExceedsOriginal() {
        // Given
        request.setTransactionType(TransactionType.REFUND);
        request.setOriginalSaleAmount(new BigDecimal("500.00"));
        request.setAmount(new BigDecimal("600.00"));

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertTrue(result.getErrors().contains("refund amount cannot exceed original sale amount"));
    }

    @Test
    void testValidateTransaction_RefundValid() {
        // Given
        request.setTransactionType(TransactionType.REFUND);
        request.setOriginalSaleAmount(new BigDecimal("1000.00"));
        request.setAmount(new BigDecimal("500.00"));

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertTrue(result.isStatus());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidateTransaction_MissingDate() {
        // Given
        request.setDate(null);

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("Date is Missing", result.getErrors().get(0));
        assertNull(result.getParsedDate());
    }

    @Test
    void testValidateTransaction_InvalidDateFormat() {
        // Given
        request.setDate("invalid-date");

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(1, result.getErrors().size());
        assertEquals("invalid date format", result.getErrors().get(0));
        assertNull(result.getParsedDate());
    }

    @Test
    void testValidateTransaction_MultipleErrors() {
        // Given
        request.setTransactionId(null);
        request.setAmount(null);
        request.setDate("invalid");

        // When
        ValidationResult result = validationService.validateTransaction(request);

        // Then
        assertFalse(result.isStatus());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().contains("transactionId is required"));
        assertTrue(result.getErrors().contains("amount is required"));
        assertTrue(result.getErrors().contains("invalid date format"));
    }
}
