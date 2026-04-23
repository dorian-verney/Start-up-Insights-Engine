package com.insight.database.entity;

import com.insight.database.enums.AddOnType;
import com.insight.database.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PAYMENT_RECORD")
public class PaymentRecord implements Serializable {

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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company;

}
