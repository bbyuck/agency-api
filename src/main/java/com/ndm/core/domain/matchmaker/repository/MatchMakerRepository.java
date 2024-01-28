package com.ndm.core.domain.matchmaker.repository;

import com.ndm.core.entity.MatchMaker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchMakerRepository extends JpaRepository<MatchMaker, Long> {
    Optional<MatchMaker> findByMatchMakerToken(String matchMakerToken);
}
