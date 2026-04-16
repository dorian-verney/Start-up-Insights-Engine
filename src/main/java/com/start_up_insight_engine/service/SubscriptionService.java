package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.database.entity.Subscriber;
import com.start_up_insight_engine.repository.*;
import com.start_up_insight_engine.transport.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.start_up_insight_engine.database.enums.Trigger;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private MrrSnapshotRepository mrrSnapshotRepository;

    @Autowired
    private ChurnSnapshotRepository churnSnapshotRepository;

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PlanRepository planRepository;


    public void handleSubscriptionStarted(SubscriptionStartedEvent event) {
        // 1. New subscriber to persist in database
        Subscriber sub = Subscriber.builder()
                .name(event.getSubscriberName())
                .email(event.getSubscriberEmail())
                .plan(planRepository.findByPlanType(event.getPlantype()))
                .subscribedAt(event.getEventTime())
                .build();
        subscriberRepository.save(sub);

        // Mettre à jour le MRR → nouveau MrrSnapshot
        Optional<MrrSnapshot> lastOne = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOne.isPresent())  lastAmount = lastOne.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(event.getMrrAmount()))
                .delta(event.getMrrAmount())
                .reason(Trigger.SUB_STARTED)
                .build();
        mrrSnapshotRepository.save(mrr);

        // Mettre à jour le churn → incrémenter active_subs
        // Calculer le LTV théorique → nouveau LtvSnapshot

    }

    public void handleSubscriptionCancelled(SubscriptionCancelledEvent event) {

    }

    public void handlePlanChanged(PlanChangedEvent event) {

    }
}
