package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.*;
import com.start_up_insight_engine.database.enums.PaymentType;
import com.start_up_insight_engine.database.enums.Trigger;
import com.start_up_insight_engine.repository.*;
import com.start_up_insight_engine.transport_kafka.OneTimePaymentEvent;
import com.start_up_insight_engine.transport_kafka.PaymentFailedEvent;
import com.start_up_insight_engine.transport_kafka.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Autowired
    private RunwaySnapshotRepository runwaySnapshotRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;


    public void handlePaymentSucceeded(PaymentSucceededEvent event){
        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. add a payment record
        PaymentRecord payment = PaymentRecord.builder()
                .subscriber(sub)
                .timestamp(event.getEventTime())
                .price(event.getAmount())
                .paymentType(event.getPaymentType())
                .build();
        paymentRecordRepository.save(payment);

        // 2. update runway -> cash + amount
        Optional<RunwaySnapshot> lastRunAway = runwaySnapshotRepository.findTopByOrderByTimestampDesc();

        BigDecimal newLiquidity = new BigDecimal(event.getAmount());
        BigDecimal totalCost = BigDecimal.ZERO;
        if (lastRunAway.isPresent()){
            newLiquidity = newLiquidity.add(lastRunAway.get().getLiquidity());
            totalCost = lastRunAway.get().getTotalCost();
        }

        Double newRunway = totalCost.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : newLiquidity.doubleValue() / totalCost.doubleValue();

        RunwaySnapshot runwaySnapshot = RunwaySnapshot.builder()
                .timestamp(event.getEventTime())
                .liquidity(newLiquidity)
                .totalCost(totalCost)
                .runway(newRunway)
                .reason(Trigger.PAYMENT_SUCCEEDED)
                .build();
        runwaySnapshotRepository.save(runwaySnapshot);
    }

    public void handlePaymentFailed(PaymentFailedEvent event){
        log.warn("Payment failed by {}, num attempts: {} ",
                event.getSubscriberId(), event.getAttemptNumber());
    }

    public void handleOneTimePayment(OneTimePaymentEvent event){
        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. add a payment record
        PaymentRecord payment = PaymentRecord.builder()
                .subscriber(sub)
                .timestamp(event.getEventTime())
                .price(event.getAmount())
                .paymentType(PaymentType.ADDON)
                .build();
        paymentRecordRepository.save(payment);

        // 2. update runway -> cash + amount
        Optional<RunwaySnapshot> lastRunAway = runwaySnapshotRepository.findTopByOrderByTimestampDesc();

        BigDecimal newLiquidity = new BigDecimal(event.getAmount());
        BigDecimal totalCost = BigDecimal.ZERO;
        if (lastRunAway.isPresent()){
            newLiquidity = newLiquidity.add(lastRunAway.get().getLiquidity());
            totalCost = lastRunAway.get().getTotalCost();
        }

        Double newRunway = totalCost.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : newLiquidity.doubleValue() / totalCost.doubleValue();

        RunwaySnapshot runwaySnapshot = RunwaySnapshot.builder()
                .timestamp(event.getEventTime())
                .liquidity(newLiquidity)
                .totalCost(totalCost)
                .runway(newRunway)
                .reason(Trigger.ONE_TIME_PAYMENT)
                .build();
        runwaySnapshotRepository.save(runwaySnapshot);
    }
}
