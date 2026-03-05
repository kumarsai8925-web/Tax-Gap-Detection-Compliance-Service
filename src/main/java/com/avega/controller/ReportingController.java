package com.avega.controller;

import com.avega.service.reporting.ExceptionReportingService;
import com.avega.service.reporting.ReportingService;
import com.avega.utils.dto.report.CustomerExceptionCountDTO;
import com.avega.utils.dto.report.CustomerTaxSummaryDTO;
import com.avega.utils.dto.report.SeverityCountDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final ExceptionReportingService exceptionReportingService;

    public ReportingController(ReportingService reportingService, ExceptionReportingService exceptionReportingService) {
        this.reportingService = reportingService;
        this.exceptionReportingService = exceptionReportingService;
    }

    @GetMapping("/customer-tax-summary")
    public ResponseEntity<List<CustomerTaxSummaryDTO>> getCustomerSummary() {
        return ResponseEntity.ok(
                reportingService.getCustomerTaxSummary()
        );
    }

    @GetMapping("/exception-summary/total")
    public ResponseEntity<Long> totalExceptions() {
        return ResponseEntity.ok(
                exceptionReportingService.getTotalExceptions()
        );
    }

    @GetMapping("/exception-summary/by-severity")
    public ResponseEntity<List<SeverityCountDTO>> severitySummary() {
        return ResponseEntity.ok(
                exceptionReportingService.getSeveritySummary()
        );
    }

    @GetMapping("/exception-summary/by-customer")
    public ResponseEntity<List<CustomerExceptionCountDTO>> customerSummary() {
        return ResponseEntity.ok(
                exceptionReportingService.getCustomerExceptionSummary()
        );
    }
}
