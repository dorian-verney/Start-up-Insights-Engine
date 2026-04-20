package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LTV_SNAPSHOT")
public class LtvSnapshot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double amountTheoric;

    @Column(nullable = false)
    private BigDecimal amountReal;

    @Column(nullable = false)
    private Trigger reason;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;
}
