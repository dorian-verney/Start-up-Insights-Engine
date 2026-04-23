package com.insight.repository;

import com.insight.database.entity.Plan;
import com.insight.database.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long>  {

    Optional<Plan> findByPlanType(PlanType planType);

    @Modifying
    @Query("UPDATE Plan p SET p.price = :newPrice WHERE p.planType = :planType")
    void updatePriceByPlanType(@Param("planType") PlanType planType, @Param("price") Integer newPrice);
}
