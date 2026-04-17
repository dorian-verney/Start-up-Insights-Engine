package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.database.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface MrrSnapshotRepository extends JpaRepository<MrrSnapshot, Long> {

    Optional<MrrSnapshot> findTopByOrderByTimestampDesc();

    List<MrrSnapshot> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
