package com.avega.test.service.rules;
import com.avega.domain.transaction.TransactionType;
import com.avega.domain.transaction.Transactions;
import com.avega.service.rules.RefundValidationRule;
import com.avega.utils.dto.rules.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RefundValidationRuleTest {

    private RefundValidationRule rule;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rule = new RefundValidationRule();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRuleCode_shouldReturnRefundValidation() {
        assertEquals("REFUND_VALIDATION", rule.getRuleCode());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenTransactionTypeIsNotRefund() throws Exception {

        Transactions tx = new Transactions();
        tx.setTransactionType(TransactionType.SALE);
        tx.setAmount(new BigDecimal("1000"));

        JsonNode config = objectMapper.readTree("{}");

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnViolated_whenOriginalSaleAmountIsNull() throws Exception {

        Transactions tx = new Transactions();
        tx.setTransactionType(TransactionType.REFUND);
        tx.setAmount(new BigDecimal("500"));
        tx.setOriginalSaleAmount(null);

        JsonNode config = objectMapper.readTree("{}");

        RuleResult result = rule.evaluate(tx, config);

        assertTrue(result.isViolated());
        assertEquals("Original sale amount missing", result.getMessage());
    }

    @Test
    void evaluate_shouldReturnViolated_whenRefundAmountExceedsOriginalSaleAmount() throws Exception {

        Transactions tx = new Transactions();
        tx.setTransactionType(TransactionType.REFUND);
        tx.setAmount(new BigDecimal("1500"));
        tx.setOriginalSaleAmount(new BigDecimal("1000"));

        JsonNode config = objectMapper.readTree("{}");

        RuleResult result = rule.evaluate(tx, config);

        assertTrue(result.isViolated());
        assertEquals("Refund amount cannot exceed original sale amount", result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenRefundAmountEqualsOriginalSaleAmount() throws Exception {

        Transactions tx = new Transactions();
        tx.setTransactionType(TransactionType.REFUND);
        tx.setAmount(new BigDecimal("1000"));
        tx.setOriginalSaleAmount(new BigDecimal("1000"));

        JsonNode config = objectMapper.readTree("{}");

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }

    @Test
    void evaluate_shouldReturnNotViolated_whenRefundAmountLessThanOriginalSaleAmount() throws Exception {

        Transactions tx = new Transactions();
        tx.setTransactionType(TransactionType.REFUND);
        tx.setAmount(new BigDecimal("500"));
        tx.setOriginalSaleAmount(new BigDecimal("1000"));

        JsonNode config = objectMapper.readTree("{}");

        RuleResult result = rule.evaluate(tx, config);

        assertFalse(result.isViolated());
        assertNull(result.getMessage());
    }
}