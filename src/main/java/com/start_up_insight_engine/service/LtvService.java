package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.repository.LtvSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class LtvService {

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Cacheable(value = "ltv-latest")
    public Optional<LtvSnapshot> findLastOne(){
        return ltvSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    @Cacheable(value = "ltv-all")
    public List<LtvSnapshot> findAll(){
        return ltvSnapshotRepository.findAll();
    }

    @Cacheable(value = "ltv-range-to", key = "#date.toString()")
    public List<LtvSnapshot> findToMonth(LocalDateTime date){
        return ltvSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "ltv-range-from", key = "#date.toString()")
    public List<LtvSnapshot> findFromMonth(LocalDateTime date){
        return ltvSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"ltv-latest", "ltv-all", "ltv-range-to", "ltv-range-from"},
            allEntries = true
    )
    public LtvSnapshot save(LtvSnapshot snapshot) {
        return ltvSnapshotRepository.save(snapshot);
    }
}
