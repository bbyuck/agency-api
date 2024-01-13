package com.ndm.core.domain.user.service;

import com.ndm.core.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JPAQueryFactory query;

    private final UserRepository userRepository;

    public boolean isNotUserExist(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId) == null;
    }
}
