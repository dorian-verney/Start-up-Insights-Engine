package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MrrSnapshotRepository extends JpaRepository<MrrSnapshot, Long> {

    Optional<MrrSnapshot> findTopByOrderByTimestampDesc();
}
