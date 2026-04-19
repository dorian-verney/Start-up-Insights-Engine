package com.start_up_insight_engine.database.entity;

import com.start_up_insight_engine.database.enums.AddOnBillingType;
import com.start_up_insight_engine.database.enums.AddOnType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "Add_ON")
@NoArgsConstructor
@AllArgsConstructor
public class AddOn implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)  // JPA stocke valeur de l'enum (LEARNING, ..) au lieu de l'index
    private AddOnType addOnType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AddOnBillingType addOnBillingType;

    @Column(nullable = false)
    private Integer price;
}
