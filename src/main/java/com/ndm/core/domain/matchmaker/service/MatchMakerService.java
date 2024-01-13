package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.model.Current;
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
    private final Current current;

    public boolean isNotMatchMakerExist(Long kakaoId) {
        return matchMakerRepository.findByKakaoId(kakaoId) == null;
    }

    public MatchMakerDto join(MatchMakerDto newMatchMaker) {
        /**
         * 1. matchMaker 정보 저장
         */
        MatchMaker.builder()
                .lastLoginIp(current.getClientIp())
                .name("영뚜")
                .kakaoId(newMatchMaker.getKakaoId())
                .build();
        return null;
    }
}
