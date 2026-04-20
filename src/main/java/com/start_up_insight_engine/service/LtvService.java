package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.entity.LtvSnapshot;
import com.start_up_insight_engine.repository.LtvSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class LtvService {

    @Autowired
    private LtvSnapshotRepository ltvSnapshotRepository;

    @Cacheable(value = "ltv-latest", key = "#company.id")
    public Optional<LtvSnapshot> findLastOne(Company company){
        return ltvSnapshotRepository.findTopByCompanyOrderByTimestampDesc(company);
    }

    @Cacheable(value = "ltv-all", key = "#company.id")
    public List<LtvSnapshot> findAll(Company company){
        return ltvSnapshotRepository.findByCompany(company);
    }

    @Cacheable(value = "ltv-range-to", key = "#company.id + '-' + #date.toString()")
    public List<LtvSnapshot> findToMonth(Company company, LocalDateTime date){
        return ltvSnapshotRepository.findByCompanyAndTimestampBetween(
                company,
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "ltv-range-from", key = "#company.id + '-' + #date.toString()")
    public List<LtvSnapshot> findFromMonth(Company company, LocalDateTime date){
        return ltvSnapshotRepository.findByCompanyAndTimestampBetween(company, date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"ltv-latest", "ltv-all", "ltv-range-to", "ltv-range-from"},
            allEntries = true
    )
    public LtvSnapshot save(LtvSnapshot snapshot) {
        return ltvSnapshotRepository.save(snapshot);
    }
}
