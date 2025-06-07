package com.indfinvestor.app.index.repository;

import com.indfinvestor.app.index.model.entity.IndexDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDetailsRepository extends JpaRepository<IndexDetails, Long> {
    IndexDetails findByName(String name);
}
