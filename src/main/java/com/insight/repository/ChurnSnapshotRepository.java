package com.insight.repository;

import com.insight.database.entity.ChurnSnapshot;
import com.insight.database.entity.Company;
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
