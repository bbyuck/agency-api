package com.ndm.core.domain.matching.repository;

import com.ndm.core.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
