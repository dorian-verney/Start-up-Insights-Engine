package com.insight.repository;

import com.insight.database.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RunwaySnapshotRepository extends JpaRepository<RunwaySnapshot, Long> {

    Optional<RunwaySnapshot> findTopByCompanyOrderByTimestampDesc(Company company);

    List<RunwaySnapshot> findByCompany(Company company);

    List<RunwaySnapshot> findByCompanyAndTimestampBetween(
            Company company,
            LocalDateTime start,
            LocalDateTime end
    );

}
