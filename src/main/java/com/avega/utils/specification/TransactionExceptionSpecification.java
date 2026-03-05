package com.avega.utils.specification;
import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.TransactionException;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class TransactionExceptionSpecification {

    public static Specification<TransactionException> filter(
            String customerId,
            Severity severity,
            String ruleName) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null && !customerId.isBlank()) {
                predicates.add(cb.equal(root.get("customerId"), customerId));
            }

            if (severity != null) {
                predicates.add(cb.equal(root.get("severity"), severity));
            }

            if (ruleName != null && !ruleName.isBlank()) {
                predicates.add(cb.equal(root.get("ruleName"), ruleName));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
