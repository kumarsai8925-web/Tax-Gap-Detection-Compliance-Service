package com.avega.service.audit;

import com.avega.domain.audit.AuditLog;
import com.avega.domain.transaction.AuditEventType;
import com.avega.repo.audit.AuditLogRepository;

import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuditService {

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository repository,
                        ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void logEvent(AuditEventType eventType,
                         String transactionId,
                         Object detailObject) {

        try {
            AuditLog log = new AuditLog();
            log.setEventType(eventType);
            log.setTransactionId(transactionId);

            String json = objectMapper.writeValueAsString(detailObject);
            log.setDetailJson(json);

            repository.save(log);

        } catch (Exception e) {
            // Do not break main flow if audit fails
            e.printStackTrace();
        }
    }
}
