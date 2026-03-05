package com.avega.domain.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_exceptions",
        indexes = {
                @Index(name = "idx_severity", columnList = "severity"),
                @Index(name = "idx_exception_customer", columnList = "customerId")
        }
   )
public class TransactionException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exceptionId;

    private String transactionId;

    private String customerId;

    private String ruleName;

    private String description;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @CreationTimestamp()
    private LocalDateTime exceptionDate;


}
