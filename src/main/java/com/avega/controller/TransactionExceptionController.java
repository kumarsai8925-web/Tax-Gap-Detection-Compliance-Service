package com.avega.controller;


import com.avega.domain.transaction.Severity;
import com.avega.domain.transaction.TransactionException;
import com.avega.service.transaction.ExceptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/exceptions")
public class TransactionExceptionController {

    private final ExceptionService exceptionService;

    public TransactionExceptionController(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionException>> getAllExceptions(
            @PageableDefault(size = 10,
                    sort = "exceptionDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                exceptionService.filterExceptions(null, null, null, pageable)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TransactionException>> filterExceptions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String ruleName,
            @PageableDefault(size = 10,
                    sort = "exceptionDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                exceptionService.filterExceptions(customerId, severity, ruleName, pageable)
        );
    }
}