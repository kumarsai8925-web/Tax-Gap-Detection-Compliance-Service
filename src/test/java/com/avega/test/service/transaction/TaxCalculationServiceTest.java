package com.avega.test.service.transaction;

import com.avega.domain.transaction.AuditEventType;
import com.avega.domain.transaction.ComplianceStatus;
import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.Transactions;
import com.avega.service.audit.AuditService;
import com.avega.service.transaction.ExceptionService;
import com.avega.service.transaction.TaxCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaxCalculationServiceTest {

    @Mock
    private ExceptionService exceptionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TaxCalculationService taxCalculationService;

    private Transactions transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transactions();
        transaction.setTransactionId("txn123");
        transaction.setCustomerId("cust123");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setTaxRate(new BigDecimal("0.10"));
        transaction.setReportedTax(new BigDecimal("100.00"));
    }

    @Test
    void testCalculateTax_MandatoryFieldsMissing() {
        // Given
        transaction.setAmount(null);

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.NON_COMPLIANT, transaction.getComplianceStatus());
        verify(exceptionService, times(1)).logException(
                "txn123", "cust123", "TAX_CALCULATION", Severity.HIGH, "Mandatory fields missing"
        );
        verify(auditService, never()).logEvent(any(), any(), any());
    }

    @Test
    void testCalculateTax_TaxRateMissing() {
        // Given
        transaction.setTaxRate(null);

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.NON_COMPLIANT, transaction.getComplianceStatus());
        verify(exceptionService, times(1)).logException(
                "txn123", "cust123", "TAX_CALCULATION", Severity.HIGH, "Mandatory fields missing"
        );
        verify(auditService, never()).logEvent(any(), any(), any());
    }

    @Test
    void testCalculateTax_ReportedTaxMissing() {
        // Given
        transaction.setReportedTax(null);

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.NON_COMPLIANT, transaction.getComplianceStatus());
        verify(exceptionService, times(1)).logException(
                "txn123", "cust123", "TAX_CALCULATION", Severity.HIGH, "Mandatory fields missing"
        );
        verify(auditService, never()).logEvent(any(), any(), any());
    }

    @Test
    void testCalculateTax_Compliant() {
        // Given: expectedTax = 1000 * 0.10 = 100.00, gap = 100 - 100 = 0, within threshold

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.COMPLIANT, transaction.getComplianceStatus());
        assertEquals(new BigDecimal("100.00"), transaction.getExpectedTax());
        assertEquals(new BigDecimal("0.00"), transaction.getTaxGap());
        verify(exceptionService, never()).logException(any(), any(), any(), any(), any());
        verify(auditService, never()).logEvent(any(), any(), any());
    }

    @Test
    void testCalculateTax_Underpaid_LowSeverity() {
        // Given: reportedTax = 50.00, expected = 100.00, gap = 50.00 > 10000? no, but >1000? no, low
        transaction.setReportedTax(new BigDecimal("50.00"));

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.UNDERPAID, transaction.getComplianceStatus());
        assertEquals(new BigDecimal("100.00"), transaction.getExpectedTax());
        assertEquals(new BigDecimal("50.00"), transaction.getTaxGap());
        verify(auditService, times(1)).logEvent(eq(AuditEventType.TAX_COMPUTATION), eq("txn123"), any(Map.class));
        verify(exceptionService, times(1)).logException(
                "txn123", "cust123", "TAX_GAP", Severity.LOW, "Tax gap: 50.00"
        );
    }

    @Test
    void testCalculateTax_Overpaid_MediumSeverity() {
        // Given: reportedTax = 150.00, expected = 100.00, gap = -50.00, abs=50 >1000? no, low
        transaction.setReportedTax(new BigDecimal("150.00"));

        // When
        taxCalculationService.calculateTax(transaction);

        // Then
        assertEquals(ComplianceStatus.OVERPAID, transaction.getComplianceStatus());
        assertEquals(new BigDecimal("100.00"), transaction.getExpectedTax());
        assertEquals(new BigDecimal("-50.00"), transaction.getTaxGap());
        verify(auditService, times(1)).logEvent(eq(AuditEventType.TAX_COMPUTATION), eq("txn123"), any(Map.class));
        verify(exceptionService, times(1)).logException(
                "txn123", "cust123", "TAX_GAP", Severity.LOW, "Tax gap: -50.00"
        );
    }

    @Test
    void testDetermineSeverity_Medium() {
        transaction.setAmount(new BigDecimal("20000.00"));
        transaction.setTaxRate(new BigDecimal("0.10"));
        transaction.setReportedTax(new BigDecimal("999.00")); // gap=1001, medium

        taxCalculationService.calculateTax(transaction);

        verify(exceptionService, times(1)).logException(
                any(), any(), eq("TAX_GAP"), eq(Severity.MEDIUM), any()
        );
    }

    @Test
    void testDetermineSeverity_High() {
        transaction.setAmount(new BigDecimal("200000.00"));
        transaction.setTaxRate(new BigDecimal("0.10"));
        transaction.setReportedTax(new BigDecimal("9999.00")); // gap=10001, high

        taxCalculationService.calculateTax(transaction);

        verify(exceptionService, times(1)).logException(
                any(), any(), eq("TAX_GAP"), eq(Severity.HIGH), any()
        );
    }
}
