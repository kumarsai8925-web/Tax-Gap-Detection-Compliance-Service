package com.avega.utils.dto.report;

import com.avega.domain.transaction.Severity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeverityCountDTO {
    private Severity severity;
    private Long count;
}
