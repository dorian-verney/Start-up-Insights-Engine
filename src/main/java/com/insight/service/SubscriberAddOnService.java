package com.insight.service;

import com.insight.database.entity.Subscriber;
import com.insight.database.entity.SubscriberAddOn;
import com.insight.database.enums.AddOnType;
import com.insight.repository.SubscriberAddOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SubscriberAddOnService {

    @Autowired
    private SubscriberAddOnRepository subscriberAddOnRepository;

    @Cacheable(value = "subAddon-sum-active", key="#sub.id")
    public BigDecimal sumActiveAddonsBySubscriber(Subscriber sub){
        return subscriberAddOnRepository.sumActiveAddonsBySubscriber(sub);
    }

    @Cacheable(value = "subAddon-find-active", key="#sub.id + '_' + #addOnType.name()")
    public Optional<SubscriberAddOn> findActiveBySubscriberAndAddOnType(Subscriber sub, AddOnType addOnType){
        return subscriberAddOnRepository.findActiveBySubscriberAndAddOnType(sub, addOnType);
    }

    @CacheEvict(
            value = {"subAddon-sum-active", "subAddon-find-active"},
            allEntries = true
    )
    public SubscriberAddOn save(SubscriberAddOn subAddOn) {
        return subscriberAddOnRepository.save(subAddOn);
    }
}
