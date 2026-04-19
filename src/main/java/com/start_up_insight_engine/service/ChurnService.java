package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.repository.ChurnSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class ChurnService {

    @Autowired
    private ChurnSnapshotRepository churnSnapshotRepository;

    @Cacheable(value = "churn-latest")
    public Optional<ChurnSnapshot> findLastOne(){
        return churnSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    @Cacheable(value = "churn-all")
    public List<ChurnSnapshot> findAll(){
        return churnSnapshotRepository.findAll();
    }

    @Cacheable(value = "churn-range-to", key = "#date.toString()")
    public List<ChurnSnapshot> findToMonth(LocalDateTime date){
        return churnSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "churn-range-from", key = "#date.toString()")
    public List<ChurnSnapshot> findFromMonth(LocalDateTime date){
        return churnSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"churn-latest", "churn-all", "churn-range-to", "churn-range-from"},
            allEntries = true
    )
    public ChurnSnapshot save(ChurnSnapshot snapshot) {
        return churnSnapshotRepository.save(snapshot);
    }
}
