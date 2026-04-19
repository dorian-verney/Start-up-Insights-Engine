package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.database.entity.RunwaySnapshot;
import com.start_up_insight_engine.repository.RunwaySnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class RunwayService {
    @Autowired
    private RunwaySnapshotRepository runwaySnapshotRepository;

    @Cacheable(value = "runway-latest")
    public Optional<RunwaySnapshot> findLastOne(){
        return runwaySnapshotRepository.findTopByOrderByTimestampDesc();
    }

    @Cacheable(value = "runway-all")
    public List<RunwaySnapshot> findAll(){
        return runwaySnapshotRepository.findAll();
    }

    @Cacheable(value = "runway-range-to", key = "#date.toString()")
    public List<RunwaySnapshot> findToMonth(LocalDateTime date){
        return runwaySnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "runway-range-from", key = "#date.toString()")
    public List<RunwaySnapshot> findFromMonth(LocalDateTime date){
        return runwaySnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"runway-latest", "runway-all", "runway-range-to", "runway-range-from"},
            allEntries = true
    )
    public RunwaySnapshot save(RunwaySnapshot snapshot) {
        return runwaySnapshotRepository.save(snapshot);
    }
}
