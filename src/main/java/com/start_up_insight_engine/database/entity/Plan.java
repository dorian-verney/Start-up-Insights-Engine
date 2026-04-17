package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.PlanType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLAN")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanType planType;
}
