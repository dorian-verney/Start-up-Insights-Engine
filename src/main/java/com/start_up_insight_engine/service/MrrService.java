package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.dto.MrrResponse;
import com.start_up_insight_engine.repository.MrrSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MrrService {

    @Autowired
    private MrrSnapshotRepository mrrSnapshotRepository;

    public Optional<MrrSnapshot> findLastOne(){
        return mrrSnapshotRepository.findTopByOrderByTimestampDesc();
    }

    public List<MrrSnapshot> findAll(){
        return mrrSnapshotRepository.findAll();
    }

    public List<MrrSnapshot> findToMonth(LocalDateTime date){
        return mrrSnapshotRepository.findByTimestampBetween(
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    public List<MrrSnapshot> findFromMonth(LocalDateTime date){
        return mrrSnapshotRepository.findByTimestampBetween(date, LocalDateTime.now());
    }
}
