package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.AddOn;
import com.start_up_insight_engine.database.entity.RunwaySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunwaySnapshotRepository extends JpaRepository<RunwaySnapshot, Long> {
}
