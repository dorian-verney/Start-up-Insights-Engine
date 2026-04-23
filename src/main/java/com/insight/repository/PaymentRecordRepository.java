package com.insight.repository;

import com.insight.database.entity.PaymentRecord;
import com.insight.database.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long>  {

    @Query("SELECT SUM(p.price) FROM PaymentRecord p WHERE p.subscriber = :subscriber")
    BigDecimal sumBySubscriber(@Param("subscriber") Subscriber subscriber);
}
