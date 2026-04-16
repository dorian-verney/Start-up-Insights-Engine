package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RUNWAY_SNAPSHOT")
public class RunwaySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private BigDecimal liquidity;

    @Column(nullable = false)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private Double runway;

    @Column(nullable = false)
    private Trigger reason;

}
