package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MatchMakerService {

    private final JPAQueryFactory query;
    private final MatchMakerRepository matchMakerRepository;
    private final Current current;

    public MatchMakerDto join(MatchMakerDto newMatchMakerDto) {
        /**
         * MatchMakerName validation
         */
        validateMatchMakerName(newMatchMakerDto.getMatchMakerName());

        /**
         * kakaoId 가입 이력 확인
         */
        if (matchMakerRepository.findByKakaoId(newMatchMakerDto.getKakaoId()) != null) {
            throw new GlobalException(REGISTERED_MEMBER);
        }

        /**
         * 닉네임 중복 확인
         */
        if (matchMakerRepository.findByName(newMatchMakerDto.getMatchMakerName()) != null) {
            throw new GlobalException(USED_MATCH_MAKER_NAME);
        }

        /**
         * matchMaker 정보 저장
         */
        MatchMaker newMatchMaker = MatchMaker.builder()
                .lastLoginIp(current.getClientIp())
                .name(newMatchMakerDto.getMatchMakerName())
                .kakaoId(newMatchMakerDto.getKakaoId())
                .build();
        matchMakerRepository.save(newMatchMaker);

        return newMatchMakerDto;
    }

    private void validateMatchMakerName(String matchMakerName) {
        String matchMakerNameRule1 = "^[a-zA-Z0-9가-힣]*$";
        String matchMakerNameRule2 = "^[a-zA-Z0-9가-힣]{1,15}$";
        if (!matchMakerName.matches(matchMakerNameRule1)) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_1);
        }
        if (!matchMakerName.matches(matchMakerNameRule2)) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_2);
        }
    }
}
