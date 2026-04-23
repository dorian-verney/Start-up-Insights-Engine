package com.insight.service;

import com.insight.database.entity.ChurnSnapshot;
import com.insight.database.entity.Company;
import com.insight.repository.ChurnSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class ChurnService {

    @Autowired
    private ChurnSnapshotRepository churnSnapshotRepository;

    @Cacheable(value = "churn-latest", key="#company.id")
    public Optional<ChurnSnapshot> findLastOne(Company company){
        return churnSnapshotRepository.findTopByCompanyOrderByTimestampDesc(company);
    }

    @Cacheable(value = "churn-all", key = "#company.id")
    public List<ChurnSnapshot> findAll(Company company){
        return churnSnapshotRepository.findByCompany(company);
    }

    @Cacheable(value = "churn-range-to", key = "#company.id + '-' + #date.toString()")
    public List<ChurnSnapshot> findToMonth(Company company, LocalDateTime date){
        return churnSnapshotRepository.findByCompanyAndTimestampBetween(
                company,
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "churn-range-from", key = "#company.id + '-' + #date.toString()")
    public List<ChurnSnapshot> findFromMonth(Company company, LocalDateTime date){
        return churnSnapshotRepository.findByCompanyAndTimestampBetween(company, date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"churn-latest", "churn-all", "churn-range-to", "churn-range-from"},
            allEntries = true
    )
    public ChurnSnapshot save(ChurnSnapshot snapshot) {
        return churnSnapshotRepository.save(snapshot);
    }
}
