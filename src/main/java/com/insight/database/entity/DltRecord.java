package com.insight.database.entity;

import com.insight.database.enums.DltStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DLT_RECORDS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DltRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false, length = 100)
    private String originalTopic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String exceptionMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DltStatus status = DltStatus.PENDING;
}