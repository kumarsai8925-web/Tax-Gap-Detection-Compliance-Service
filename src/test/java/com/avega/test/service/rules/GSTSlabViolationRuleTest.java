package com.avega.test.service.rules;

import com.avega.domain.transaction.Transactions;
import com.avega.service.rules.GSTSlabViolationRule;
import com.avega.utils.dto.rules.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GSTSlabViolationRuleTest {

    private GSTSlabViolationRule rule;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rule = new GSTSlabViolationRule();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRuleCode_shouldReturnGSTSlab() {
        assertEquals("GST_SLAB", rule.getRuleCode());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountIsNull() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(null);
        tx.setTaxRate(new BigDecimal("5"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenTaxRateIsNull() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("2000"));
        tx.setTaxRate(null);

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnViolated_whenAmountAboveThreshold_andTaxBelowMinimum() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("2000"));
        tx.setTaxRate(new BigDecimal("5"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertTrue(result.isViolated());
        assertEquals("GST slab violation", result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountBelowThreshold() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("500"));
        tx.setTaxRate(new BigDecimal("5"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenTaxRateAboveMinimum() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("2000"));
        tx.setTaxRate(new BigDecimal("18"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenAmountEqualsThreshold() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("1000"));
        tx.setTaxRate(new BigDecimal("5"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenTaxRateEqualsMinimum() throws Exception {

        Transactions tx = new Transactions();
        tx.setAmount(new BigDecimal("2000"));
        tx.setTaxRate(new BigDecimal("12"));

        JsonNode config = objectMapper.readTree("""
                {
                  "slabThreshold": 1000,
                  "minimumTaxRate": 12
                }
                """);

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }
}