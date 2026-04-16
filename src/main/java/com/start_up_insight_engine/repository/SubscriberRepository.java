package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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


}

