package com.insight.database.entity;

import com.insight.database.enums.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLAN")
public class Plan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private PlanType planType;
}
