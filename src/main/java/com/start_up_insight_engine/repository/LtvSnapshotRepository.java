package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LtvSnapshotRepository extends JpaRepository<LtvSnapshot, Long> {
}
