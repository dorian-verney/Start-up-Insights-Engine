package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
