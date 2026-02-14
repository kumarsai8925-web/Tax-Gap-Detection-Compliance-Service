package com.avega.repo.rules;

import com.avega.domain.rules.TaxRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaxRuleRepository extends JpaRepository<TaxRuleEntity,Long> {

        List<TaxRuleEntity> findByEnabledTrue();
}
