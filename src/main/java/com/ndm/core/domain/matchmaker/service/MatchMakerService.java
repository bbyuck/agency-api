package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchMakerService {

    private final JPAQueryFactory query;
    private final MatchMakerRepository matchMakerRepository;
    private final EntityManager em;

    public boolean isNotMatchMakerExist(Long kakaoId) {
        return matchMakerRepository.findByKakaoId(kakaoId) == null;
    }

    public void join(MatchMaker newMatchMaker) {
        em.persist(newMatchMaker);
    }
}
