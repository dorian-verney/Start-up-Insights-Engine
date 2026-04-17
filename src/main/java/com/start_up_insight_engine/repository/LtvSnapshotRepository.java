package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.LtvSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LtvSnapshotRepository extends JpaRepository<LtvSnapshot, Long> {

    Optional<LtvSnapshot> findTopByOrderByTimestampDesc();
    List<LtvSnapshot> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
