package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.Plan;
import com.start_up_insight_engine.database.enums.PlanType;
import com.start_up_insight_engine.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Cacheable(value = "planType", key = "#plan.toString()")
    public Optional<Plan> findByPlanType(PlanType plan){
        return planRepository.findByPlanType(plan);
    }
}
