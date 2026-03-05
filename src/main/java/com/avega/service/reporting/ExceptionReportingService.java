package com.avega.service.reporting;

import com.avega.repo.transaction.TransactionExceptionRepository;
import com.avega.utils.dto.report.CustomerExceptionCountDTO;
import com.avega.utils.dto.report.SeverityCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExceptionReportingService {

    private final TransactionExceptionRepository repository;

    public ExceptionReportingService(TransactionExceptionRepository repository) {
        this.repository = repository;
    }

    public Long getTotalExceptions() {
        return repository.getTotalExceptions();
    }

    public List<SeverityCountDTO> getSeveritySummary() {
        return repository.countBySeverity();
    }

    public List<CustomerExceptionCountDTO> getCustomerExceptionSummary() {
        return repository.countByCustomer();
    }
}
