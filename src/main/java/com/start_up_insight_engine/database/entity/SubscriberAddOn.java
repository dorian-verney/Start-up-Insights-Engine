package com.start_up_insight_engine.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUBSCRIBER_ADD_ON")
public class SubscriberAddOn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Subscriber subscriber;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AddOn addOn;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = true)
    private LocalDateTime endedAt; // null si toujours actif

}
