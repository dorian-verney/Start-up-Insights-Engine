package com.insight.repository;

import com.insight.database.entity.AddOn;
import com.insight.database.enums.AddOnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddOnRepository extends JpaRepository<AddOn, Long> {

    Optional<AddOn> findByAddOnType(AddOnType addOnType);
}
