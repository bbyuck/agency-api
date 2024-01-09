package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ndm.core.domain.matchmaker.entity.QMatchMaker.matchMaker;
import static com.ndm.core.model.ErrorInfo.*;
import static com.ndm.core.model.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class MatchMakerService {

    private final MatchMakerRepository matchMakerRepository;

    private final JPAQueryFactory query;

    private final PasswordEncoder passwordEncoder;

    private boolean isNotAlphabet(char c, boolean upper) {
        return upper ? ('A' > c || c > 'Z') : ('a' > c || c > 'z');
    }


    @Transactional
    public MatchMakerDto signup(MatchMakerDto matchMakerDto) {
        /*
         * 1. loginId 확인
         */

        /**
         * 1.1. loginId null & empty string check
         */
        if (matchMakerDto.getLoginId() == null
                || matchMakerDto.getLoginId().isEmpty()) {
            throw new GlobalException(INVALID_LOGIN_ID_0);
        }

        /**
         * 1.2. loginId rule check
         * 1.2.1. loginId는 4글자 이상, 10글자 이하여야 한다
         * 1.2.2. loginId는 영문 대, 소문자로 시작해야 한다. (숫자로 시작 X)
         * 1.2.3. loginId는 영문 대, 소문자, 숫자로만 이루어져야 한다 (한글, 특수문자 X)
         */
        if (matchMakerDto.getLoginId().length() < 4 || matchMakerDto.getLoginId().length() > 10) {
            throw new GlobalException(INVALID_LOGIN_ID_1);
        }

        char first = matchMakerDto.getLoginId().charAt(0);
        if (isNotAlphabet(first, false)
                && isNotAlphabet(first, true)) {
            throw new GlobalException(INVALID_LOGIN_ID_2);
        }

        String idRegex = "^[a-zA-Z0-9]+$";
        if (!matchMakerDto.getLoginId().matches(idRegex)) {
            throw new GlobalException(INVALID_LOGIN_ID_3);
        }


        /*
         * 2. password 확인
         * 2.1. 8자 이상 14자 이하
         * 2.2. 영문 대/소문자, 숫자, 특수문자 !,@,#,$,%,^,&,* 만 허용
         * 2.3. 영문, 숫자, 특수문자가 반드시 하나 이상씩 들어가야 함.
         * 2.4. password와 password confirm이 일치해야함.
         */
        if (matchMakerDto.getPassword() == null || matchMakerDto.getPassword().isEmpty()) {
            throw new GlobalException(INVALID_PASSWORD_0);
        }

        if (matchMakerDto.getPassword().length() < 8 || matchMakerDto.getPassword().length() > 14) {
            throw new GlobalException(INVALID_PASSWORD_1);
        }

        String pwRegex1 = "^[a-zA-Z0-9!@#$%^&*]+$";
        if (!matchMakerDto.getPassword().matches(pwRegex1)) {
            throw new GlobalException(INVALID_PASSWORD_2);
        }

        String pwRegex2 = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$";
        if (!matchMakerDto.getPassword().matches(pwRegex2)) {
            throw new GlobalException(INVALID_PASSWORD_3);
        }

        /**
         * Password confirm 확인
         */
        if (matchMakerDto.getPasswordConfirm() == null
                || matchMakerDto.getPasswordConfirm().isEmpty()) {
            throw new GlobalException(INVALID_PASSWORD_CONFIRM_0);
        }

        if (!matchMakerDto.getPassword().equals(matchMakerDto.getPasswordConfirm())) {
            throw new GlobalException(INVALID_PASSWORD_4);
        }

        /*
         * 3. 가입 여부 확인
         */
//        MatchMaker findMatchMaker = matchMakerRepository.findUserByLoginId(matchMakerDto.getLoginId());
        MatchMaker findMatchMaker = query.selectFrom(matchMaker)
                .where(matchMaker.loginId.eq(matchMakerDto.getLoginId()))
                .fetchOne();

        if (findMatchMaker != null) {
            throw new GlobalException(REGISTERED_USER);
        }

        MatchMaker newMatchMaker = MatchMaker.builder()
                .loginId(matchMakerDto.getLoginId())
                .password(passwordEncoder.encode(matchMakerDto.getPassword()))
                .role(ROLE_USER)
                .build();

        MatchMaker savedMatchMaker = matchMakerRepository.save(newMatchMaker);

        return MatchMakerDto.builder()
                .loginId(savedMatchMaker.getLoginId())
                .build();
    }

}
