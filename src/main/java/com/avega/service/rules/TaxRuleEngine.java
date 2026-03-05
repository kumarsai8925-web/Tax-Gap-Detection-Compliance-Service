package com.avega.service.rules;

import com.avega.domain.rules.TaxRuleEntity;
import com.avega.domain.transaction.AuditEventType;
import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.Transactions;
import com.avega.repo.rules.TaxRuleRepository;

import com.avega.service.audit.AuditService;
import com.avega.service.transaction.ExceptionService;
import com.avega.utils.dto.rules.RuleResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TaxRuleEngine {

    private final TaxRuleRepository ruleRepository;
    private final Map<String, TaxRule> ruleMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionService exceptionService;
    private final AuditService auditService;

    public TaxRuleEngine(TaxRuleRepository ruleRepository,
                         List<TaxRule> rules,
                         ExceptionService exceptionService, AuditService auditService) {

        this.ruleRepository = ruleRepository;
        this.exceptionService = exceptionService;
        this.auditService = auditService;

        for (TaxRule rule : rules) {
            ruleMap.put(rule.getRuleCode(), rule);
        }
    }

    public List<String> evaluate(Transactions tx) {

        List<String> violations = new ArrayList<>();
        List<TaxRuleEntity> activeRules = ruleRepository.findByEnabledTrue();

        for (TaxRuleEntity ruleEntity : activeRules) {

            TaxRule rule = ruleMap.get(ruleEntity.getRuleCode());
            if (rule == null) continue;

            try {
                JsonNode config =
                        objectMapper.readTree(ruleEntity.getRuleConfigJson());

                RuleResult result = rule.evaluate(tx, config);

                Map<String, Object> detail = new HashMap<>();
                detail.put("ruleName", rule.getRuleCode());
                detail.put("violated", result.isViolated());
                detail.put("message", result.getMessage());

                auditService.logEvent(
                        AuditEventType.RULE_EXECUTION,
                        tx.getTransactionId(),
                        detail
                );

                if (result.isViolated()) {

                    violations.add(result.getMessage());


                    exceptionService.logException(
                            tx.getTransactionId(),
                            tx.getCustomerId(),
                            ruleEntity.getRuleCode(),
                            Severity.MEDIUM,
                            result.getMessage()
                    );
                }

            } catch (Exception e) {

                violations.add("Rule execution failed: "
                        + ruleEntity.getRuleCode());

                exceptionService.logException(
                        tx.getTransactionId(),
                        tx.getCustomerId(),
                        ruleEntity.getRuleCode(),
                        Severity.HIGH,
                        "Rule execution failed"
                );
            }
        }
        return violations;
    }
}