package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    
    // Méthode dérivée (Spring génère la requête)
    Optional<Subscriber> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Subscriber> findByCancelledAtIsNull();

    List<Subscriber> findByCancelledAtIsNotNull();

    List<Subscriber> findByCancelledAtBetween(LocalDateTime start, LocalDateTime end);

//    @Query("SELECT SUM(p.price) FROM Subscriber s JOIN PaymentRecord p ON
//            p WHERE s.id = p.subscriberId AND s = :subscriber")
//    BigDecimal sumBySubscriber(@Param("subscriber") Subscriber subscriber);

}

