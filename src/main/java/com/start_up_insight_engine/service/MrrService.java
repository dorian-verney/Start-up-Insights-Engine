package com.start_up_insight_engine.service;

import com.start_up_insight_engine.database.entity.Company;
import com.start_up_insight_engine.database.entity.MrrSnapshot;
import com.start_up_insight_engine.repository.MrrSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MrrService {

    @Autowired
    private MrrSnapshotRepository mrrSnapshotRepository;

    @Cacheable(value = "mrr-latest", key = "#company.id")
    public Optional<MrrSnapshot> findLastOne(Company company){
        return mrrSnapshotRepository.findTopByCompanyOrderByTimestampDesc(company);
    }

    @Cacheable(value = "mrr-all", key = "#company.id")
    public List<MrrSnapshot> findAll(Company company){
        return mrrSnapshotRepository.findByCompany(company);
    }

    @Cacheable(value = "mrr-range-to", key = "#company.id + '-' + #date.toString()")
    public List<MrrSnapshot> findToMonth(Company company, LocalDateTime date){
        return mrrSnapshotRepository.findByCompanyAndTimestampBetween(
                company,
                LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                date);
    }

    @Cacheable(value = "mrr-range-from", key = "#company.id + '-' + #date.toString()")
    public List<MrrSnapshot> findFromMonth(Company company, LocalDateTime date){
        return mrrSnapshotRepository.findByCompanyAndTimestampBetween(company, date, LocalDateTime.now());
    }

    // Invalidate cache — (example : called by handleSubscriptionStarted)
    @CacheEvict(
            value = {"mrr-latest", "mrr-all", "mrr-range-to", "mrr-range-from"},
            allEntries = true
    )
    public MrrSnapshot save(MrrSnapshot snapshot) {
        return mrrSnapshotRepository.save(snapshot);
    }
}
