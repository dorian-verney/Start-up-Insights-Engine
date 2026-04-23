package com.insight.service;

import com.insight.database.entity.*;
import com.insight.exceptions.EventProcessingException;
import com.insight.repository.*;
import com.insight.transport_kafka.PlanChangedEvent;
import com.insight.transport_kafka.SubscriptionCancelledEvent;
import com.insight.transport_kafka.SubscriptionStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.insight.database.enums.Trigger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {

    @Lazy
    @Autowired
    private SubscriptionService self;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private MrrService mrrService;

    @Autowired
    private ChurnService churnService;

    @Autowired
    private LtvService ltvService;

    @Autowired
    private PlanService planService;

    @Autowired
    private Helper helper;


    @Cacheable(value = "subscriber-id", key = "#id")
    public Optional<Subscriber> findById(long id){
        return subscriberRepository.findById(id);
    }

    @Cacheable(value = "subscriber-cancelled-between", key = "#start.toString() + '_' + #end.toString()")
    public List<Subscriber> findByCancelledAtBetween(LocalDateTime start, LocalDateTime end){
        return subscriberRepository.findByCancelledAtBetween(start, end);
    }

    @Cacheable(value = "subscriber-cancelled-null")
    public List<Subscriber> findByCancelledAtIsNull(){
        return subscriberRepository.findByCancelledAtIsNull();
    }

    @CacheEvict(
            value = {"subscriber-id", "subscriber-cancelled-null", "subscriber-cancelled-between"},
            allEntries = true
    )
    public Subscriber save(Subscriber sub) {
        return subscriberRepository.save(sub);
    }


    public void handleSubscriptionStarted(SubscriptionStartedEvent event) throws EventProcessingException {
        Company company = helper.requireCompany(event.getCompanyId());

        // 1. New subscriber to persist in database
        Plan plan = planService.findByPlanType(event.getPlanType())
                .orElseThrow(() -> new IllegalArgumentException("Unknown plan type: " + event.getPlanType()));

        Subscriber sub = Subscriber.builder()
                .name(event.getSubscriberName())
                .email(event.getSubscriberEmail())
                .plan(plan)
                .subscribedAt(event.getEventTime())
                .company(company)
                .build();
        self.save(sub);

        // 2. Update MRR → new MrrSnapshot
        BigDecimal mrrAmount = event.getMrrAmount() != null
                ? event.getMrrAmount()
                : new BigDecimal(plan.getPrice());

        BigDecimal lastAmount = helper.lastMrrAmount(company);

        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(mrrAmount))
                .delta(mrrAmount)
                .reason(Trigger.SUB_STARTED)
                .company(company)
                .build();
        mrrService.save(mrr);

        // 3. Update churn → increment active subs
        Long lastActiveSubscribers = helper.lastChurnActives(company);
        float lastRate             = helper.lastChurnRate(company);

        ChurnSnapshot churn = ChurnSnapshot.builder()
                .timestamp(event.getEventTime())
                .rate(lastRate)
                .activeSubscribers(lastActiveSubscribers+1)
                .reason(Trigger.SUB_STARTED)
                .company(company)
                .build();
        churnService.save(churn);

        // 4. Compute theoric LTV → new LtvSnapshot
        BigDecimal lastAmountReal = helper.lastLtvAmountReal(company);

        Double theoricLtv = lastRate == 0F
                ? mrrAmount.doubleValue()
                : mrrAmount.doubleValue() / lastRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.SUB_STARTED)
                .company(company)
                .build();
        ltvService.save(ltv);
    }

    public void handleSubscriptionCancelled(SubscriptionCancelledEvent event) throws EventProcessingException {
        Company company = helper.requireCompany(event.getCompanyId());
        Subscriber sub = helper.requireSubscriber(event.getSubscriberId());

        // 1. Update MRR → new MrrSnapshot
        BigDecimal lastAmount = helper.lastMrrAmount(company);
        BigDecimal lastMrrSub = new BigDecimal(sub.getPlan().getPrice());

        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.subtract(lastMrrSub))
                .delta(lastMrrSub.negate())
                .reason(Trigger.SUB_CANCELLED)
                .company(company)
                .build();
        mrrService.save(mrr);


        // 2. Update churn → decrement active subs + increment churn this month
        Long lastActiveSubscribers = helper.lastChurnActives(company);

        LocalDateTime startOfMonth = event.getEventTime()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        float numLostSubThisMonth  = self.findByCancelledAtBetween(startOfMonth, event.getEventTime()).size();
        float activeStartOMonth = self.findByCancelledAtIsNull().size() + numLostSubThisMonth;

        float newRate = numLostSubThisMonth == 0F
                ? 0F
                : activeStartOMonth / numLostSubThisMonth;

        ChurnSnapshot churn = ChurnSnapshot.builder()
                .timestamp(event.getEventTime())
                .rate(newRate)
                .activeSubscribers(lastActiveSubscribers-1)
                .reason(Trigger.SUB_CANCELLED)
                .company(company)
                .build();
        churnService.save(churn);


        // 3. Update LTV → LTV real + theoric
        BigDecimal mrrSubTotal = paymentRecordService.sumBySubscriber(sub);
        Double theoricLtv = newRate == 0F
                ? mrrSubTotal.doubleValue()
                : mrrSubTotal.doubleValue() / newRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(mrrSubTotal)
                .reason(Trigger.SUB_CANCELLED)
                .company(company)
                .build();
        ltvService.save(ltv);

        // 4. update cancelledAt in Subscriber
        sub.setCancelledAt(LocalDateTime.now());
        self.save(sub);
    }

    public void handlePlanChanged(PlanChangedEvent event) throws EventProcessingException {

        Company company = helper.requireCompany(event.getCompanyId());
        Subscriber sub = helper.requireSubscriber(event.getSubscriberId());

        // 1. Update MRR → new MrrSnapshot
        BigDecimal lastAmount = helper.lastMrrAmount(company);

        BigDecimal diffPricePlan = event.getNewMrrAmount().subtract(event.getOldMrrAmount());
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(diffPricePlan))
                .delta(diffPricePlan)
                .reason(Trigger.PLAN_CHANGED)
                .company(company)
                .build();
        mrrService.save(mrr);

        // 2. Update LTV theoric
        Optional<ChurnSnapshot> lastOneChurn = churnService.findLastOne(company);

        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        BigDecimal lastAmountReal = helper.lastLtvAmountReal(company);

        Double theoricLtv = lastRate == 0F
                ? event.getNewMrrAmount().doubleValue()
                : event.getNewMrrAmount().doubleValue() / lastRate;

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.PLAN_CHANGED)
                .company(company)
                .build();
        ltvService.save(ltv);

        // 3. update Plan in Subscriber
        sub.setPlan(event.getNewPlan());
        self.save(sub);
    }
}
