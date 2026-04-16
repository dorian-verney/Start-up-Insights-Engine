package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LTV_SNAPSHOT")
public class LtvSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private BigDecimal amountTheoric;

    @Column(nullable = false)
    private BigDecimal amountReal;

    @Column(nullable = false)
    private Trigger reason;
}
