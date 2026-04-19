package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.*;
import com.start_up_insight_engine.database.enums.AddOnType;
import com.start_up_insight_engine.database.enums.Trigger;
import com.start_up_insight_engine.repository.*;
import com.start_up_insight_engine.transport_kafka.AddOnAddedEvent;
import com.start_up_insight_engine.transport_kafka.AddOnRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class AddOnService {

    @Autowired
    private AddOnRepository addOnRepository;


    @Lazy
    @Autowired
    private AddOnService self;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriberAddOnService subscriberAddOnService;

    @Autowired
    private MrrService mrrService;

    @Autowired
    private LtvService ltvService;

    @Autowired
    private ChurnService churnService;

    @Cacheable(value = "addon-type", key = "#addOnType.toString()")
    public Optional<AddOn> findByAddOnType(AddOnType addOnType){
        return addOnRepository.findByAddOnType(addOnType);
    }


    public void handleAddonAdded(AddOnAddedEvent event) {
        Optional<Subscriber> opSub = subscriptionService.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. update MRR + mrr_amount
        Optional<MrrSnapshot> lastOneMrr = mrrService.findLastOne();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOneMrr.isPresent())  lastAmount = lastOneMrr.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(event.getMrrAmount()))
                .delta(event.getMrrAmount())
                .reason(Trigger.ADDON_ADDED)
                .build();
        mrrService.save(mrr);

        // 2. update LTV theoric = mrr_amount / churn_rate global
        Optional<ChurnSnapshot> lastOneChurn = churnService.findLastOne();
        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        BigDecimal addonSum = subscriberAddOnService.sumActiveAddonsBySubscriber(sub);
        Double sumAddonPlanSub = addonSum == null ? 0.0 : addonSum.doubleValue();

        sumAddonPlanSub += sub.getPlan().getPrice();

        Double theoricLtv = lastRate == 0F
                ? sumAddonPlanSub
                : sumAddonPlanSub / lastRate;

        Optional<LtvSnapshot> lastOneLtv = ltvService.findLastOne();
        BigDecimal lastAmountReal = BigDecimal.ZERO;
        if (lastOneLtv.isPresent()){
            lastAmountReal  = lastOneLtv.get().getAmountReal();
        }

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.ADDON_ADDED)
                .build();
        ltvService.save(ltv);


        // 3. add a record in SubscriberAddOn
        Optional<AddOn> opAddOn = self.findByAddOnType(event.getAddOnType());
        if (opAddOn.isEmpty()) {
            log.warn("AddOn not found: {}", event.getAddOnType());
            return;
        }
        AddOn addOn = opAddOn.get();
        SubscriberAddOn subAddOn = SubscriberAddOn.builder()
                .subscriber(sub)
                .addOn(addOn)
                .startedAt(LocalDateTime.now())
                .endedAt(null)
                .build();

        subscriberAddOnService.save(subAddOn);
    }

    public void handleAddonRemoved(AddOnRemovedEvent event) {
        Optional<Subscriber> opSub = subscriptionService.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. update MRR + mrr_amount
        Optional<MrrSnapshot> lastOneMrr = mrrService.findLastOne();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOneMrr.isPresent())  lastAmount = lastOneMrr.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.subtract(event.getMrrAmount()))
                .delta(event.getMrrAmount().negate())
                .reason(Trigger.ADDON_REMOVED)
                .build();
        mrrService.save(mrr);

        // 2. update LTV theoric = mrr_amount / churn_rate global
        Optional<ChurnSnapshot> lastOneChurn = churnService.findLastOne();
        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        BigDecimal addonSum = subscriberAddOnService.sumActiveAddonsBySubscriber(sub);
        Double sumAddonPlanSub = addonSum == null ? 0.0 : addonSum.doubleValue();

        sumAddonPlanSub += sub.getPlan().getPrice();

        // removed the AddOn to be removed to compute Ltv
        sumAddonPlanSub -= event.getMrrAmount().doubleValue();

        Double theoricLtv = lastRate == 0F
                ? sumAddonPlanSub
                : sumAddonPlanSub / lastRate;

        Optional<LtvSnapshot> lastOneLtv = ltvService.findLastOne();
        BigDecimal lastAmountReal = BigDecimal.ZERO;
        if (lastOneLtv.isPresent()){
            lastAmountReal  = lastOneLtv.get().getAmountReal();
        }

        LtvSnapshot ltv = LtvSnapshot.builder()
                .timestamp(event.getEventTime())
                .amountTheoric(theoricLtv)
                .amountReal(lastAmountReal)
                .reason(Trigger.ADDON_REMOVED)
                .build();
        ltvService.save(ltv);


        // 3. set remove in SubscriberAddOn

        Optional<SubscriberAddOn> subAddOn = subscriberAddOnService
                .findActiveBySubscriberAndAddOnType(sub, event.getAddOnType());

        if (subAddOn.isPresent()){
            subAddOn.get().setEndedAt(LocalDateTime.now());
            subscriberAddOnService.save(subAddOn.get());
        }

    }
}
