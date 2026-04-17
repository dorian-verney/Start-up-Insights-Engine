package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.repository.ChurnSnapshotRepository;
import com.start_up_insight_engine.repository.LtvSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class LtvService {
    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    public Optional<LtvSnapshot> findLastOne(){
        return ltvSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    public List<LtvSnapshot> findAll(){
        return ltvSnapshotRepository.findAll();
    }

    public List<LtvSnapshot> findToMonth(LocalDateTime date){
        return ltvSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    public List<LtvSnapshot> findFromMonth(LocalDateTime date){
        return ltvSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }
}
