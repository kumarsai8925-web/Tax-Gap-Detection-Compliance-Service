package com.avega.domain.rules;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tax_rule")
public class TaxRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleCode;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "TEXT")
    private String ruleConfigJson;

}
