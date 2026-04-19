package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.repository.MrrSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MrrService {

    @Autowired
    private MrrSnapshotRepository mrrSnapshotRepository;

    @Cacheable(value = "mrr-latest")
    public Optional<MrrSnapshot> findLastOne(){
        return mrrSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    @Cacheable(value = "mrr-all")
    public List<MrrSnapshot> findAll(){
        return mrrSnapshotRepository.findAll();
    }

    @Cacheable(value = "mrr-range-to", key = "#date.toString()")
    public List<MrrSnapshot> findToMonth(LocalDateTime date){
        return mrrSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "mrr-range-from", key = "#date.toString()")
    public List<MrrSnapshot> findFromMonth(LocalDateTime date){
        return mrrSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"mrr-latest", "mrr-all", "mrr-range-to", "mrr-range-from"},
            allEntries = true
    )
    public MrrSnapshot save(MrrSnapshot snapshot) {
        return mrrSnapshotRepository.save(snapshot);
    }
}
