package com.avega.utils.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerExceptionCountDTO {
    private String customerId;
    private Long count;
}
