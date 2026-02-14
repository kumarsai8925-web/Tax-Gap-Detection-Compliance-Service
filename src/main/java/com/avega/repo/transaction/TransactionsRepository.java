package com.avega.repo.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avega.domain.transaction.Transactions;
@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long>{

}
