package com.avega.utils.dto.rules;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleResult {
        private boolean violated;
        private String message;
}
