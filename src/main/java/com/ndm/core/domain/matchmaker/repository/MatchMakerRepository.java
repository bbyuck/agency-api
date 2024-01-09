package com.ndm.core.domain.matchmaker.repository;

import com.ndm.core.domain.matchmaker.entity.MatchMaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MatchMakerRepository extends JpaRepository<MatchMaker, Long> {
}
