package com.insight.service;

import com.insight.database.entity.Plan;
import com.insight.database.enums.PlanType;
import com.insight.repository.PlanRepository;
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
