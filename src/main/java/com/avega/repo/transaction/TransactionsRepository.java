package com.avega.repo.transaction;

import com.avega.utils.dto.report.CustomerTaxSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.avega.domain.transaction.Transactions;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long>{

    @Query("""
        SELECT new com.avega.utils.dto.report.CustomerTaxSummaryDTO(
            t.customerId,
            SUM(t.amount),
            SUM(t.reportedTax),
            SUM(t.expectedTax),
            SUM(t.taxGap),
            (100.0 - 
                (SUM(CASE WHEN t.complianceStatus <> 'COMPLIANT' THEN 1 ELSE 0 END) 
                * 100.0 / COUNT(t))
            )
        )
        FROM Transactions t
        WHERE t.validationStatus = 'SUCCESS'
        GROUP BY t.customerId
    """)
    List<CustomerTaxSummaryDTO> getCustomerTaxSummary();

}
