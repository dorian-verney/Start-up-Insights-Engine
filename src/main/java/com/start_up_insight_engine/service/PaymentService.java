package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.*;
import com.start_up_insight_engine.database.enums.PaymentType;
import com.start_up_insight_engine.database.enums.Trigger;
import com.start_up_insight_engine.exceptions.EventProcessingException;
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
    private RunwayService runwayService;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private Helper helper;



    public void handlePaymentSucceeded(PaymentSucceededEvent event) throws EventProcessingException{
        Subscriber sub = helper.requireSubscriber(event.getSubscriberId());
        Company company = helper.requireCompany(event.getCompanyId());

        // 1. add a payment record
        PaymentRecord payment = PaymentRecord.builder()
                .subscriber(sub)
                .timestamp(event.getEventTime())
                .price(event.getAmount())
                .paymentType(event.getPaymentType())
                .company(company)
                .build();
        paymentRecordService.save(payment);

        // 2. update runway -> cash + amount
        Optional<RunwaySnapshot> lastRunAway = runwayService.findLastOne(company);

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
                .company(company)
                .build();
        runwayService.save(runwaySnapshot);
    }

    public void handlePaymentFailed(PaymentFailedEvent event){
        log.warn("Payment failed by {}, num attempts: {} ",
                event.getSubscriberId(), event.getAttemptNumber());
    }

    public void handleOneTimePayment(OneTimePaymentEvent event) throws EventProcessingException  {

        Subscriber sub = helper.requireSubscriber(event.getSubscriberId());
        Company company = helper.requireCompany(event.getCompanyId());

        // 1. add a payment record
        PaymentRecord payment = PaymentRecord.builder()
                .subscriber(sub)
                .timestamp(event.getEventTime())
                .price(event.getAmount())
                .paymentType(PaymentType.ADDON)
                .company(company)
                .build();
        paymentRecordService.save(payment);

        // 2. update runway -> cash + amount
        Optional<RunwaySnapshot> lastRunAway = runwayService.findLastOne(company);

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
                .company(company)
                .build();
        runwayService.save(runwaySnapshot);
    }
}
