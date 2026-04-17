package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.*;
import com.start_up_insight_engine.repository.*;
import com.start_up_insight_engine.transport_kafka.PlanChangedEvent;
import com.start_up_insight_engine.transport_kafka.SubscriptionCancelledEvent;
import com.start_up_insight_engine.transport_kafka.SubscriptionStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.start_up_insight_engine.database.enums.Trigger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
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
    @Autowired
    private PaymentRecordRepository paymentRecordRepository;


    public void handleSubscriptionStarted(SubscriptionStartedEvent event) {

        // 1. New subscriber to persist in database
        Plan plan = planRepository.findByPlanType(event.getPlantype())
                .orElseThrow(() -> new IllegalArgumentException("Unknown plan type: " + event.getPlantype()));

        Subscriber sub = Subscriber.builder()
                .name(event.getSubscriberName())
                .email(event.getSubscriberEmail())
                .plan(plan)
                .subscribedAt(event.getEventTime())
                .build();
        subscriberRepository.save(sub);

        // 2. Update MRR → new MrrSnapshot
        Optional<MrrSnapshot> lastOneMrr = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOneMrr.isPresent())  lastAmount = lastOneMrr.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(event.getMrrAmount()))
                .delta(event.getMrrAmount())
                .reason(Trigger.SUB_STARTED)
                .build();
        mrrSnapshotRepository.save(mrr);

        // 3. Update churn → increment active subs
        Optional<ChurnSnapshot> lastOneChurn = churnSnapshotRepository.findTopByOrderByTimestampDesc();
        Long lastActiveSubscribers = 0L;
        float lastRate             = 0F;
        if (lastOneChurn.isPresent()){
            lastActiveSubscribers = lastOneChurn.get().getActiveSubscribers();
            lastRate             = lastOneChurn.get().getRate();
        }

        ChurnSnapshot churn = ChurnSnapshot.builder()
                .timestamp(event.getEventTime())
                .rate(lastRate)
                .activeSubscribers(lastActiveSubscribers+1)
                .reason(Trigger.SUB_STARTED)
                .build();
        churnSnapshotRepository.save(churn);

        // 4. Compute theoric LTV → new LtvSnapshot
        Optional<LtvSnapshot> lastOneLtv = ltvSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmountReal = BigDecimal.ZERO;
        if (lastOneLtv.isPresent()){
            lastAmountReal    = lastOneLtv.get().getAmountReal();
        }
        Double theoricLtv = lastRate == 0F
                ? event.getMrrAmount().doubleValue()
                : event.getMrrAmount().doubleValue() / lastRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.SUB_STARTED)
                .build();
        ltvSnapshotRepository.save(ltv);
    }

    public void handleSubscriptionCancelled(SubscriptionCancelledEvent event) {

        // 1. Update MRR → new MrrSnapshot
        Optional<MrrSnapshot> lastOneMrr = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = lastOneMrr.isEmpty()
                 ? BigDecimal.ZERO
                 : lastOneMrr.get().getAmount();

        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();
        BigDecimal lastMrrSub = new BigDecimal(sub.getPlan().getPrice());

        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.subtract(lastMrrSub))
                .delta(lastMrrSub.negate())
                .reason(Trigger.SUB_CANCELLED)
                .build();
        mrrSnapshotRepository.save(mrr);


        // 2. Update churn → decrement active subs + increment churn this month
        Optional<ChurnSnapshot> lastOneChurn = churnSnapshotRepository.findTopByOrderByTimestampDesc();
        Long lastActiveSubscribers = lastOneChurn.isEmpty()
                ? 0L
                : lastOneChurn.get().getActiveSubscribers();


        LocalDateTime startOfMonth = event.getEventTime()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        float numLostSubThisMonth  = subscriberRepository.findByCancelledAtBetween(startOfMonth, event.getEventTime()).size();
        float activeStartOMonth = subscriberRepository.findByCancelledAtIsNull().size() + numLostSubThisMonth;

        float newRate = numLostSubThisMonth == 0F
                ? 0F
                : activeStartOMonth / numLostSubThisMonth;

        ChurnSnapshot churn = ChurnSnapshot.builder()
                .timestamp(event.getEventTime())
                .rate(newRate)
                .activeSubscribers(lastActiveSubscribers-1)
                .reason(Trigger.SUB_CANCELLED)
                .build();
        churnSnapshotRepository.save(churn);


        // 3. Update LTV → LTV real + theoric
        BigDecimal mrrSubTotal = paymentRecordRepository.sumBySubscriber(sub);
        Double theoricLtv = newRate == 0F
                ? mrrSubTotal.doubleValue()
                : mrrSubTotal.doubleValue() / newRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(mrrSubTotal)
                .reason(Trigger.SUB_CANCELLED)
                .build();
        ltvSnapshotRepository.save(ltv);

        // 4. update cancelledAt in Subscriber
        sub.setCancelledAt(LocalDateTime.now());
        subscriberRepository.save(sub);
    }

    public void handlePlanChanged(PlanChangedEvent event) {
        // 1. Update MRR → new MrrSnapshot
        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        Optional<MrrSnapshot> lastOneMrr = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = lastOneMrr.isEmpty()
                ? BigDecimal.ZERO
                : lastOneMrr.get().getAmount();

        BigDecimal diffPricePlan = event.getNewMrrAmount().subtract(event.getOldMrrAmount());
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(diffPricePlan))
                .delta(diffPricePlan)
                .reason(Trigger.PLAN_CHANGED)
                .build();
        mrrSnapshotRepository.save(mrr);

        // 2. Update LTV theoric
        Optional<ChurnSnapshot> lastOneChurn = churnSnapshotRepository.findTopByOrderByTimestampDesc();

        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        Optional<LtvSnapshot> lastOneLtv = ltvSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmountReal = BigDecimal.ZERO;
        if (lastOneLtv.isPresent()){
            lastAmountReal    = lastOneLtv.get().getAmountReal();
        }
        Double theoricLtv = lastRate == 0F
                ? event.getNewMrrAmount().doubleValue()
                : event.getNewMrrAmount().doubleValue() / lastRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.PLAN_CHANGED)
                .build();
        ltvSnapshotRepository.save(ltv);

        // 3. update Plan in Subscriber
        sub.setPlan(event.getNewPlan());
        subscriberRepository.save(sub);
    }
}
