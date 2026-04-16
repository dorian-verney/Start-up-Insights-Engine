package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Plan;
import com.start_up_insight_engine.database.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanRepository extends JpaRepository<Plan, Long>  {

    Plan findByPlanType(PlanType planType);

    @Modifying
    @Query("UPDATE Plan p SET p.price = :newPrice WHERE p.planType = :planType")
    void updatePriceByPlanType(@Param("planType") PlanType planType, @Param("price") Integer newPrice);
}
