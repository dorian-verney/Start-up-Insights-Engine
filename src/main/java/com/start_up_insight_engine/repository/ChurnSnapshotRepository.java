package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.AddOn;
import com.start_up_insight_engine.database.entity.ChurnSnapshot;
import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChurnSnapshotRepository extends JpaRepository<ChurnSnapshot, Long>  {

    Optional<ChurnSnapshot> findTopByCompanyOrderByTimestampDesc(Company company);

    List<ChurnSnapshot> findByCompany(Company company);

    List<ChurnSnapshot> findByCompanyAndTimestampBetween(
            Company company,
            LocalDateTime start,
            LocalDateTime end
    );
}
