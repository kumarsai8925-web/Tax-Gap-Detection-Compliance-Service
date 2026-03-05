package com.avega.repo.transaction;

import com.avega.domain.transaction.TransactionException;
import com.avega.utils.dto.report.CustomerExceptionCountDTO;
import com.avega.utils.dto.report.SeverityCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionExceptionRepository extends
        JpaRepository<TransactionException, Long>,
        JpaSpecificationExecutor<TransactionException> {

    @Query("SELECT COUNT(e) FROM TransactionException e")
    Long getTotalExceptions();

    @Query("""
        SELECT new com.avega.utils.dto.report.SeverityCountDTO(
            e.severity,
            COUNT(e)
        )
        FROM TransactionException e
        GROUP BY e.severity
    """)
    List<SeverityCountDTO> countBySeverity();

    @Query("""
        SELECT new com.avega.utils.dto.report.CustomerExceptionCountDTO(
            e.customerId,
            COUNT(e)
        )
        FROM TransactionException e
        GROUP BY e.customerId
    """)
    List<CustomerExceptionCountDTO> countByCustomer();
}