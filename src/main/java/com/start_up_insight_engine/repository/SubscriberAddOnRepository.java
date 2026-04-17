package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Subscriber;
import com.start_up_insight_engine.database.entity.SubscriberAddOn;
import com.start_up_insight_engine.database.enums.AddOnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SubscriberAddOnRepository extends JpaRepository<SubscriberAddOn, Long> {

    List<SubscriberAddOn> findByEndedAtIsNull();

    List<SubscriberAddOn> findByEndedAtIsNullAndSubscriber(Subscriber subscriber);

    @Query("SELECT SUM(a.addOn.price) FROM SubscriberAddOn a WHERE a.subscriber = :subscriber AND a.endedAt IS NULL")
    BigDecimal sumActiveAddonsBySubscriber(@Param("subscriber") Subscriber subscriber);

    @Query("SELECT sa FROM SubscriberAddOn sa WHERE sa.subscriber = :subscriber AND sa.addOn.addOnType = :addOnType AND sa.endedAt IS NULL")
    Optional<SubscriberAddOn> findActiveBySubscriberAndAddOnType(@Param("subscriber") Subscriber subscriber, @Param("addOnType") AddOnType addOnType);
}
