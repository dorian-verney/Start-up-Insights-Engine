package com.insight.service;

import com.insight.database.entity.PaymentRecord;
import com.insight.database.entity.Subscriber;
import com.insight.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentRecordService {

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Cacheable(value = "paymentRecord-sum", key="#subscriber.id")
    public BigDecimal sumBySubscriber(Subscriber subscriber){
        return paymentRecordRepository.sumBySubscriber(subscriber);
    }

    @CacheEvict(
            value = {"paymentRecord-sum"},
            allEntries = true
    )
    public PaymentRecord save(PaymentRecord payment) {
        return paymentRecordRepository.save(payment);
    }
}
