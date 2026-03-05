package com.avega.test.service.rules;
import com.avega.domain.rules.TaxRuleEntity;
import com.avega.domain.transaction.AuditEventType;
import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.Transactions;
import com.avega.repo.rules.TaxRuleRepository;
import com.avega.service.audit.AuditService;
import com.avega.service.rules.TaxRule;
import com.avega.service.rules.TaxRuleEngine;
import com.avega.service.transaction.ExceptionService;
import com.avega.utils.dto.rules.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxRuleEngineTest {

    @Mock
    private TaxRuleRepository ruleRepository;

    @Mock
    private ExceptionService exceptionService;

    @Mock
    private AuditService auditService;

    @Mock
    private TaxRule taxRule;

    private TaxRuleEngine taxRuleEngine;

    private Transactions tx;

    @BeforeEach
    void setUp() {
        tx = new Transactions();
        tx.setTransactionId("TXN123");
        tx.setCustomerId("CUST1");

        when(taxRule.getRuleCode()).thenReturn("RULE_1");

        taxRuleEngine = new TaxRuleEngine(
                ruleRepository,
                List.of(taxRule),
                exceptionService,
                auditService
        );
    }

    //  No Active Rules
    @Test
    void evaluate_shouldReturnEmpty_whenNoActiveRules() {

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(Collections.emptyList());

        List<String> result = taxRuleEngine.evaluate(tx);

        assertTrue(result.isEmpty());
        verifyNoInteractions(exceptionService);
        verifyNoInteractions(auditService);
    }

    //  Rule Not Violated
    @Test
    void evaluate_shouldNotLogException_whenRuleNotViolated() throws Exception {

        TaxRuleEntity entity = new TaxRuleEntity();
        entity.setRuleCode("RULE_1");
        entity.setRuleConfigJson("{\"limit\":100}");

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(List.of(entity));

        RuleResult ruleResult = new RuleResult(false, "Within limit");

        when(taxRule.evaluate(any(), any(JsonNode.class)))
                .thenReturn(ruleResult);

        List<String> result = taxRuleEngine.evaluate(tx);

        assertTrue(result.isEmpty());

        verify(auditService).logEvent(
                eq(AuditEventType.RULE_EXECUTION),
                eq("TXN123"),
                any()
        );

        verify(exceptionService, never()).logException(
                any(), any(), any(), any(), any()
        );
    }

    //  Rule Violated
    @Test
    void evaluate_shouldLogException_whenRuleViolated() throws Exception {

        TaxRuleEntity entity = new TaxRuleEntity();
        entity.setRuleCode("RULE_1");
        entity.setRuleConfigJson("{\"limit\":100}");

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(List.of(entity));

        RuleResult ruleResult = new RuleResult(true, "Limit exceeded");

        when(taxRule.evaluate(any(), any(JsonNode.class)))
                .thenReturn(ruleResult);

        List<String> result = taxRuleEngine.evaluate(tx);

        assertEquals(1, result.size());
        assertEquals("Limit exceeded", result.get(0));

        verify(exceptionService).logException(
                eq("TXN123"),
                eq("CUST1"),
                eq("RULE_1"),
                eq(Severity.MEDIUM),
                eq("Limit exceeded")
        );
    }

    //  Rule Not Found in Map
    @Test
    void evaluate_shouldSkip_whenRuleNotFoundInMap() {

        TaxRuleEntity entity = new TaxRuleEntity();
        entity.setRuleCode("UNKNOWN_RULE");
        entity.setRuleConfigJson("{}");

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(List.of(entity));

        List<String> result = taxRuleEngine.evaluate(tx);

        assertTrue(result.isEmpty());
        verifyNoInteractions(exceptionService);
    }

    //  Exception During Rule Execution
    @Test
    void evaluate_shouldLogHighSeverity_whenJsonParsingFails() {

        TaxRuleEntity entity = new TaxRuleEntity();
        entity.setRuleCode("RULE_1");
        entity.setRuleConfigJson("INVALID_JSON");

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(List.of(entity));

        List<String> result = taxRuleEngine.evaluate(tx);

        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Rule execution failed"));

        verify(exceptionService).logException(
                eq("TXN123"),
                eq("CUST1"),
                eq("RULE_1"),
                eq(Severity.HIGH),
                eq("Rule execution failed")
        );
    }

    // Multiple Rules Mixed Results
    @Test
    void evaluate_shouldHandleMultipleRules() throws Exception {

        TaxRuleEntity rule1 = new TaxRuleEntity();
        rule1.setRuleCode("RULE_1");
        rule1.setRuleConfigJson("{}");

        TaxRuleEntity rule2 = new TaxRuleEntity();
        rule2.setRuleCode("RULE_1");
        rule2.setRuleConfigJson("{}");

        when(ruleRepository.findByEnabledTrue())
                .thenReturn(List.of(rule1, rule2));

        when(taxRule.evaluate(any(), any(JsonNode.class)))
                .thenReturn(
                        new RuleResult(true, "Violation 1"),
                        new RuleResult(false, "OK")
                );

        List<String> result = taxRuleEngine.evaluate(tx);

        assertEquals(1, result.size());
        assertEquals("Violation 1", result.get(0));
    }
}