package com.avega.domain.audit;

import com.avega.domain.transaction.AuditEventType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    private String transactionId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String detailJson;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
