package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.repository.ChurnSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class ChurnService {

    @Autowired
    private ChurnSnapshotRepository churnSnapshotRepository;

    public Optional<ChurnSnapshot> findLastOne(){
        return churnSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    public List<ChurnSnapshot> findAll(){
        return churnSnapshotRepository.findAll();
    }

    public List<ChurnSnapshot> findToMonth(LocalDateTime date){
        return churnSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    public List<ChurnSnapshot> findFromMonth(LocalDateTime date){
        return churnSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }
}
