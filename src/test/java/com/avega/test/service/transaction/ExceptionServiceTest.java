package com.avega.test.service.transaction;

import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.TransactionException;
import com.avega.repo.transaction.TransactionExceptionRepository;
import com.avega.service.transaction.ExceptionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExceptionServiceTest {

    @Mock
    private TransactionExceptionRepository exceptionRepository;

    @InjectMocks
    private ExceptionService exceptionService;

    private TransactionException transactionException;

    @BeforeEach
    void setUp() {
        transactionException = new TransactionException();
        transactionException.setTransactionId("txn123");
        transactionException.setCustomerId("cust123");
        transactionException.setRuleName("TestRule");
        transactionException.setSeverity(Severity.HIGH);
        transactionException.setDescription("Test exception");
    }

    @Test
    void testLogException() {
        // When
        exceptionService.logException("txn123", "cust123", "TestRule", Severity.HIGH, "Test exception");

        // Then
        verify(exceptionRepository, times(1)).save(any(TransactionException.class));
    }

    @Test
    void testFilterExceptions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<TransactionException> exceptions = List.of(transactionException);
        Page<TransactionException> page = new PageImpl<>(exceptions, pageable, 1);

        when(exceptionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // When
        Page<TransactionException> result = exceptionService.filterExceptions("cust123", Severity.HIGH, "TestRule", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(exceptionRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFilterExceptionsWithNullParameters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<TransactionException> exceptions = List.of(transactionException);
        Page<TransactionException> page = new PageImpl<>(exceptions, pageable, 1);

        when(exceptionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // When
        Page<TransactionException> result = exceptionService.filterExceptions(null, null, null, pageable);

        // Then
        assertNotNull(result);
        verify(exceptionRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
