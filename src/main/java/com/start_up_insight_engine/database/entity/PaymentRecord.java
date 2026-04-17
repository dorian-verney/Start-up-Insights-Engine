package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.AddOnType;
import com.start_up_insight_engine.database.enums.PaymentType;
import com.start_up_insight_engine.database.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@Table(name = "PAYMENT_RECORD")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Subscriber subscriber;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)  // JPA stocke valeur de l'enum (LEARNING, ..) au lieu de l'index
    private PaymentType paymentType;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AddOnType addOnType;

}
