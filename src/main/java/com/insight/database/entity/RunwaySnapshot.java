package com.insight.database.entity;

import com.insight.database.enums.Trigger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RUNWAY_SNAPSHOT")
public class RunwaySnapshot implements Serializable {

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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

}
