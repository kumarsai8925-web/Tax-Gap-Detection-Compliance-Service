package com.avega.utils.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CustomerTaxSummaryDTO {

    private String customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalReportedTax;
    private BigDecimal totalExpectedTax;
    private BigDecimal totalTaxGap;
    private Double complianceScore;
}
