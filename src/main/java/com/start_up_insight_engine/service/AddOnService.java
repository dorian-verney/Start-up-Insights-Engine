package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.*;
import com.start_up_insight_engine.database.enums.Trigger;
import com.start_up_insight_engine.repository.*;
import com.start_up_insight_engine.transport_kafka.AddOnAddedEvent;
import com.start_up_insight_engine.transport_kafka.AddOnRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class AddOnService {

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private MrrSnapshotRepository mrrSnapshotRepository;

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Autowired
    private ChurnSnapshotRepository churnSnapshotRepository;

    @Autowired
    private SubscriberAddOnRepository subscriberAddOnRepository;


    public void handleAddonAdded(AddOnAddedEvent event) {
        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. update MRR + mrr_amount
        Optional<MrrSnapshot> lastOneMrr = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOneMrr.isPresent())  lastAmount = lastOneMrr.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.add(event.getMrrAmount()))
                .delta(event.getMrrAmount())
                .reason(Trigger.ADDON_ADDED)
                .build();
        mrrSnapshotRepository.save(mrr);

        // 2. update LTV theoric = mrr_amount / churn_rate global
        Optional<ChurnSnapshot> lastOneChurn = churnSnapshotRepository.findTopByOrderByTimestampDesc();
        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        BigDecimal addonSum = subscriberAddOnRepository.sumActiveAddonsBySubscriber(sub);
        Double sumAddonPlanSub = addonSum == null ? 0.0 : addonSum.doubleValue();

        sumAddonPlanSub += sub.getPlan().getPrice();

        Double theoricLtv = lastRate == 0F
                ? sumAddonPlanSub
                : sumAddonPlanSub / lastRate;

        Optional<LtvSnapshot> lastOneLtv = ltvSnapshotRepository.findTopByOrderByTimestampDesc();
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
        ltvSnapshotRepository.save(ltv);


        // 3. add a record in SubscriberAddOn
        Optional<AddOn> opAddOn = addOnRepository.findByAddOnType(event.getAddOnType());
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

        subscriberAddOnRepository.save(subAddOn);
    }

    public void handleAddonRemoved(AddOnRemovedEvent event) {
        Optional<Subscriber> opSub = subscriberRepository.findById(event.getSubscriberId());
        if (opSub.isEmpty()) {
            log.warn("Subscriber not found: {}", event.getSubscriberId());
            return;
        }
        Subscriber sub = opSub.get();

        // 1. update MRR + mrr_amount
        Optional<MrrSnapshot> lastOneMrr = mrrSnapshotRepository.findTopByOrderByTimestampDesc();
        BigDecimal lastAmount = BigDecimal.ZERO;
        if (lastOneMrr.isPresent())  lastAmount = lastOneMrr.get().getAmount();
        MrrSnapshot mrr = MrrSnapshot.builder()
                .timestamp(event.getEventTime())
                .amount(lastAmount.subtract(event.getMrrAmount()))
                .delta(event.getMrrAmount().negate())
                .reason(Trigger.ADDON_REMOVED)
                .build();
        mrrSnapshotRepository.save(mrr);

        // 2. update LTV theoric = mrr_amount / churn_rate global
        Optional<ChurnSnapshot> lastOneChurn = churnSnapshotRepository.findTopByOrderByTimestampDesc();
        float lastRate = lastOneChurn.map(ChurnSnapshot::getRate).orElse(0F);

        BigDecimal addonSum = subscriberAddOnRepository.sumActiveAddonsBySubscriber(sub);
        Double sumAddonPlanSub = addonSum == null ? 0.0 : addonSum.doubleValue();

        sumAddonPlanSub += sub.getPlan().getPrice();

        // removed the AddOn to be removed to compute Ltv
        sumAddonPlanSub -= event.getMrrAmount().doubleValue();

        Double theoricLtv = lastRate == 0F
                ? sumAddonPlanSub
                : sumAddonPlanSub / lastRate;

        Optional<LtvSnapshot> lastOneLtv = ltvSnapshotRepository.findTopByOrderByTimestampDesc();
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
        ltvSnapshotRepository.save(ltv);


        // 3. set remove in SubscriberAddOn

        Optional<SubscriberAddOn> subAddOn = subscriberAddOnRepository
                .findActiveBySubscriberAndAddOnType(sub, event.getAddOnType());

        if (subAddOn.isPresent()){
            subAddOn.get().setEndedAt(LocalDateTime.now());
            subscriberAddOnRepository.save(subAddOn.get());
        }

    }
}
