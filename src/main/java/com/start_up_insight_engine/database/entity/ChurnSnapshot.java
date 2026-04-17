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
@Table(name = "CHURN_SNAPSHOT")
public class ChurnSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private float rate;

    @Column(nullable = false)
    private Long activeSubcribers;

    @Column(nullable = false)
    private Trigger reason;
}
