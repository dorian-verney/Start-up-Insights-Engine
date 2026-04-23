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

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MRR_SNAPSHOT")
public class MrrSnapshot implements Serializable {

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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;
}