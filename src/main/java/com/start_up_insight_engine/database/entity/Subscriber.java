package com.start_up_insight_engine.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUBSCRIBER")
public class Subscriber implements Serializable {
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

    @Setter
    @Column(nullable = true)
    private LocalDateTime cancelledAt; // null si toujours actif

}
