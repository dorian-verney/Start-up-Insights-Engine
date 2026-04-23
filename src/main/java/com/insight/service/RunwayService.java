package com.insight.service;

import com.insight.database.entity.Company;
import com.insight.database.entity.RunwaySnapshot;
import com.insight.repository.RunwaySnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class RunwayService {

    @Autowired
    private RunwaySnapshotRepository runwaySnapshotRepository;

    @Cacheable(value = "runway-latest", key = "#company.id")
    public Optional<RunwaySnapshot> findLastOne(Company company){
        return runwaySnapshotRepository.findTopByCompanyOrderByTimestampDesc(company);
    }

    @Cacheable(value = "runway-all", key = "#company.id")
    public List<RunwaySnapshot> findAll(Company company){
        return runwaySnapshotRepository.findByCompany(company);
    }

    @Cacheable(value = "runway-range-to", key = "#company.id + '-' + #date.toString()")
    public List<RunwaySnapshot> findToMonth(Company company, LocalDateTime date){
        return runwaySnapshotRepository.findByCompanyAndTimestampBetween(
                company,
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "runway-range-from", key = "#company.id + '-' + #date.toString()")
    public List<RunwaySnapshot> findFromMonth(Company company, LocalDateTime date){
        return runwaySnapshotRepository.findByCompanyAndTimestampBetween(company, date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"runway-latest", "runway-all", "runway-range-to", "runway-range-from"},
            allEntries = true
    )
    public RunwaySnapshot save(RunwaySnapshot snapshot) {
        return runwaySnapshotRepository.save(snapshot);
    }
}
