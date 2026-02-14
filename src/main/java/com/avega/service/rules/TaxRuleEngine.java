package com.avega.service.rules;

import com.avega.domain.rules.TaxRuleEntity;
import com.avega.domain.transaction.Transactions;
import com.avega.repo.rules.TaxRuleRepository;
import com.avega.utils.rules.RuleResult;
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
    private final Map<String,TaxRule> ruleMap;
    private final ObjectMapper objectMapper= new ObjectMapper();


    public TaxRuleEngine(TaxRuleRepository ruleRepository,
                         List<TaxRule> rules) {

        this.ruleRepository = ruleRepository;
        this.ruleMap = new HashMap<>();

        for(TaxRule rule : rules){
            ruleMap.put(rule.getRuleCode(),rule);
        }

    }

    public List<String> evaluate(Transactions tx) {

        List<String> violations = new ArrayList<>();

        List<TaxRuleEntity> activeRules = ruleRepository.findByEnabledTrue();

        for (TaxRuleEntity ruleEntity :  activeRules ){

            TaxRule rule = ruleMap.get(ruleEntity.getRuleCode());

            if (rule == null) continue;

            try {
                JsonNode config = objectMapper.readTree(ruleEntity.getRuleConfigJson());

                RuleResult result = rule.evaluate(tx,config);

                if (result.isViolated()){

                    violations.add(result.getMessage());

                }
            }catch (Exception e){
                violations.add("Rule execution failed: " +
                        ruleEntity.getRuleCode());

            }
        }
        return violations;
    }

}
