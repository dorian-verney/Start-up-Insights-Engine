package com.start_up_insight_engine.database.entity;


import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name = "MRR_SNAPSHOT")
public class MrrSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private BigDecimal amount; // mrr total qui s'ajoute

    @Column(nullable = false)
    private BigDecimal delta;  // variation avec new action

    @Column(nullable = false)
    private Trigger reason;
}