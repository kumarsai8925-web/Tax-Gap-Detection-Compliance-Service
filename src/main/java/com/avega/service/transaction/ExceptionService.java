package com.avega.service.transaction;

import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.TransactionException;
import com.avega.repo.transaction.TransactionExceptionRepository;
import com.avega.utils.specification.TransactionExceptionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ExceptionService {

    private final TransactionExceptionRepository exceptionRepository;

    public ExceptionService(TransactionExceptionRepository exceptionRepository) {
        this.exceptionRepository = exceptionRepository;
    }

    public void logException(String transactionId,
                             String customerId,
                             String ruleName,
                             Severity severity,
                             String message) {

        TransactionException ex = new TransactionException();
        ex.setTransactionId(transactionId);
        ex.setCustomerId(customerId);
        ex.setRuleName(ruleName);
        ex.setSeverity(severity);
        ex.setDescription(message);

        exceptionRepository.save(ex);
    }

    public Page<TransactionException> filterExceptions(
            String customerId,
            Severity severity,
            String ruleName,
            Pageable pageable) {

        Specification<TransactionException> spec =
                TransactionExceptionSpecification.filter(customerId, severity, ruleName);

        return exceptionRepository.findAll(spec, pageable);
    }
}