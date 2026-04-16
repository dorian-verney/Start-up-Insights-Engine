package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Subscriber;
import com.start_up_insight_engine.database.entity.SubscriberAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriberAddOnRepository extends JpaRepository<SubscriberAddOn, Long> {

    List<SubscriberAddOn> findByEndedAtIsNull();

    List<SubscriberAddOn> findByEndedAtIsNullAndSubscriber(Subscriber subscriber);
}
