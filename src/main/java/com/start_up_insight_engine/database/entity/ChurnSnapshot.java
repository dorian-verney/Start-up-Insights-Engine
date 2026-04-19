package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.Trigger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHURN_SNAPSHOT")
public class ChurnSnapshot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private float rate;

    @Column(nullable = false)
    private Long activeSubscribers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Trigger reason;
}
