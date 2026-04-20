package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LtvSnapshotRepository extends JpaRepository<LtvSnapshot, Long> {


    Optional<LtvSnapshot> findTopByCompanyOrderByTimestampDesc(Company company);

    List<LtvSnapshot> findByCompany(Company company);

    List<LtvSnapshot> findByCompanyAndTimestampBetween(
            Company company,
            LocalDateTime start,
            LocalDateTime end
    );
}
