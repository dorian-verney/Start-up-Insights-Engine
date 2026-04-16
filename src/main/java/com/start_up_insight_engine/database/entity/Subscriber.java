package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.PlanType;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "SUBSCRIBER")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private LocalDateTime subscribedAt;

    @Column(nullable = true)
    private LocalDateTime cancelledAt; // null si toujours actif

}
