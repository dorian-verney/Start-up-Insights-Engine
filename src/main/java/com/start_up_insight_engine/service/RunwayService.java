package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.RunwaySnapshot;
import com.start_up_insight_engine.repository.RunwaySnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class RunwayService {
    @Autowired
    private RunwaySnapshotRepository runwaySnapshotRepository;

    public Optional<RunwaySnapshot> findLastOne(){
        return runwaySnapshotRepository.findTopByOrderByTimestampDesc();
    }

    public List<RunwaySnapshot> findAll(){
        return runwaySnapshotRepository.findAll();
    }

    public List<RunwaySnapshot> findToMonth(LocalDateTime date){
        return runwaySnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    public List<RunwaySnapshot> findFromMonth(LocalDateTime date){
        return runwaySnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }
}
