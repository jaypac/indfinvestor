package com.indfinvestor.app.indexprocessor.repository;

import com.indfinvestor.app.indexprocessor.entity.IndexReturnStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexReturnStatsRepository extends JpaRepository<IndexReturnStats, Long> {
}
