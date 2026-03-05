package com.avega.service.reporting;

import com.avega.repo.transaction.TransactionsRepository;
import com.avega.utils.dto.report.CustomerTaxSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingService {

    private final TransactionsRepository transactionRepository;

    public ReportingService(TransactionsRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<CustomerTaxSummaryDTO> getCustomerTaxSummary() {
        return transactionRepository.getCustomerTaxSummary();
    }
}
