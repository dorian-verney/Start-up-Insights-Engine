package com.insight.repository;

import com.insight.database.entity.DltRecord;
import com.insight.database.enums.DltStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DltRecordRepository extends JpaRepository<DltRecord, Long> {

    // Idempotence : DO NOT duplicate if same id
    Optional<DltRecord> findByEventId(UUID eventId);

    List<DltRecord> findByStatusOrderByFailedAtDesc(DltStatus status);

    List<DltRecord> findByCompanyIdAndStatusOrderByFailedAtDesc(
            Long companyId, DltStatus status);
}