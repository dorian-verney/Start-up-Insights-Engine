package com.insight.poller.translator;

import com.insight.database.enums.PlanType;
import com.insight.poller.client.dto.CrmSubscriptionDto;
import com.insight.transport_kafka.PlanChangedEvent;
import com.insight.transport_kafka.SubscriptionCancelledEvent;
import com.insight.transport_kafka.SubscriptionStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

// Translator Subscription from Mock to Kafka event Subscription

@Component
@Slf4j
public class SubscriptionEventTranslator {

    // TODO: tenantId is dynamic
    private static final Long TENANT_ID = 1L;

    public SubscriptionStartedEvent toSubscriptionStarted(CrmSubscriptionDto dto) {

        return SubscriptionStartedEvent.builder()

                .eventId(UUID.randomUUID())                    // TODO danger idempotence
                .eventType("SubscriptionStarted")
                .eventTime(LocalDateTime.now())
                .subscriberId(Long.parseLong(dto.getId().replace("sub_", "")))
                .companyId(TENANT_ID)
                .subscriberEmail(dto.getEmail())
                .subscriberName(dto.getEmail()) // TODO
                .planType(PlanType.valueOf(dto.getPlan())) // TODO
                .mrrAmount(dto.getAmountMonthly()) // TODO
                .build();
    }
    // TODO implem plan changed and cancelled

//    public PlanChangedEvent toPlanChanged(CrmSubscriptionDto dto) {
//
//        return PlanChangedEvent.builder()
//
//                .eventId(UUID.randomUUID())                    // TODO danger idempotence
//                .eventType("SubscriptionStarted")
//                .eventTime(LocalDateTime.now())
//                .subscriberId(Long.parseLong(dto.getId().replace("sub_", "")))
//                .companyId(TENANT_ID)
//                .oldPlan()
//                .newPlan()
//                .oldMrrAmount()
//                .newMrrAmount()
//                .build();
//    }
//
//    public SubscriptionCancelledEvent toSubscriptionCancelled(CrmSubscriptionDto dto) {
//
//        return SubscriptionCancelledEvent.builder()
//                .eventId(UUID.randomUUID())                    // TODO danger idempotence
//                .eventType("SubscriptionStarted")
//                .eventTime(LocalDateTime.now())
//                .subscriberId(Long.parseLong(dto.getId().replace("sub_", "")))
//                .companyId(TENANT_ID)
//                .build();
//    }
}