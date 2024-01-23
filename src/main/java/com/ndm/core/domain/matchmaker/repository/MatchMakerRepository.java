package com.ndm.core.domain.matchmaker.repository;

import com.ndm.core.entity.MatchMaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchMakerRepository extends JpaRepository<MatchMaker, Long> {
    MatchMaker findByOauthId(String oauthId);
}
