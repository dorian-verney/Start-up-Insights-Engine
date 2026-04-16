package com.start_up_insight_engine.repository;

import com.start_up_insight_engine.database.entity.AddOn;
import com.start_up_insight_engine.database.entity.Plan;
import com.start_up_insight_engine.database.enums.AddOnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddOnRepository extends JpaRepository<AddOn, Long> {

    Optional<AddOn> findByAddOnType(AddOnType addOnType);
}
