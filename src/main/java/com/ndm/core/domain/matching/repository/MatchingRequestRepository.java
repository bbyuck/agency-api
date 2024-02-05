package com.ndm.core.domain.matching.repository;

import com.ndm.core.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {
}
