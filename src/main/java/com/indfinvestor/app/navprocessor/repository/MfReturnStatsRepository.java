package com.indfinvestor.app.navprocessor.repository;

import com.indfinvestor.app.navprocessor.entity.MfReturnStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfReturnStatsRepository extends JpaRepository<MfReturnStats, Long> {
}
