package com.avega.utils.dto.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RuleResult {
        private boolean violated;
        private String message;
}
