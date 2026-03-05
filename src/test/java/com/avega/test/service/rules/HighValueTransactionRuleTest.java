package com.avega.test.service.rules;

import com.avega.domain.transaction.Transactions;
import com.avega.service.rules.HighValueTransactionRule;
import com.avega.utils.dto.rules.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class HighValueTransactionRuleTest {

    private HighValueTransactionRule rule;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rule = new HighValueTransactionRule();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRuleCode_shouldReturnHighValueTxn() {
        assertEquals("HIGH_VALUE_TXN", rule.getRuleCode());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountIsNull() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(null);

        JsonNode config = objectMapper.readTree("""
                {
                  "threshold": 10000
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnViolated_whenAmountGreaterThanThreshold() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("15000"));

        JsonNode config = objectMapper.readTree("""
                {
                  "threshold": 10000
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertTrue(result.isViolated());
        assertEquals("High value transaction", result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountLessThanThreshold() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("5000"));

        JsonNode config = objectMapper.readTree("""
                {
                  "threshold": 10000
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountEqualsThreshold() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("10000"));

        JsonNode config = objectMapper.readTree("""
                {
                  "threshold": 10000
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }
}
