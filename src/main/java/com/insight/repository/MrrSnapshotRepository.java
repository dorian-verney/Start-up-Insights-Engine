package com.insight.repository;

import com.insight.database.entity.Company;
import com.insight.database.entity.MrrSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MrrSnapshotRepository extends JpaRepository<MrrSnapshot, Long> {

    Optional<MrrSnapshot> findTopByCompanyOrderByTimestampDesc(Company company);

    List<MrrSnapshot> findByCompany(Company company);

    List<MrrSnapshot> findByCompanyAndTimestampBetween(
            Company company,
            LocalDateTime start,
            LocalDateTime end
    );
}
