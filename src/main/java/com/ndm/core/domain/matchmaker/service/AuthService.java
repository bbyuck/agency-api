package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.domain.matchmaker.dto.LoginDto;
import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import com.ndm.core.domain.matchmaker.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.entity.QMatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.AlreadyGetJwtTokenException;
import com.ndm.core.model.exception.GlobalException;
import com.ndm.core.model.exception.NotAuthenticatedException;
import com.ndm.core.security.JwtTokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ndm.core.domain.matchmaker.entity.QMatchMaker.*;
import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "userAuthentication")
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MatchMakerRepository matchMakerRepository;

    private final JPAQueryFactory query;
    private final Current current;

    @Transactional
    public TokenInfo reissueJwtToken(TokenInfo clientTokenInfo) {
        Authentication authentication = jwtTokenProvider.getAuthentication(clientTokenInfo.getAccessToken());
        User principal = (User) authentication.getPrincipal();
        MatchMaker matchMaker = getMatchMakerForAuthentication(principal.getUsername());

        /*
         * 1. 클라이언트로부터 받은 refresh token이 해당 유저가 보유중인 refresh token인지 체크
         */
        if (!matchMaker.getRefreshToken().equals(clientTokenInfo.getRefreshToken())) {
            throw new GlobalException(INVALID_TOKEN);
        }

        /*
         * 2. refresh token expire check
         */
        jwtTokenProvider.validateRefreshToken(clientTokenInfo.getRefreshToken());

        /*
         * 3. 마지막 로그인 ip와 reissue 요청 ip 비교
         * TODO : Web Fingerprint 등 기기 관리 필요
         */
        String lastLoginIp = matchMaker.getLastLoginIp();
        if (!lastLoginIp.equals(current.getClientIp())) {
            throw new GlobalException(IP_ADDRESS_CHANGED);
        }

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        updateUserTokenInfo(matchMaker.getLoginId(), tokenInfo);

        return tokenInfo;
    }

    @Transactional
    public TokenInfo issueJwtToken(LoginDto loginDto) {
        String loginId = loginDto.getLoginId();
        String password = loginDto.getPassword();

        /**
         * 1. loginId null validation
         */
        if (loginId == null || loginId.isEmpty()) {
            throw new GlobalException(INVALID_LOGIN_ID_0);
        }

        /**
         * 2. password null validation
         */
        if (password == null || password.isEmpty()) {
            throw new GlobalException(INVALID_PASSWORD_0);
        }


        updateUserLastLoginInfo(loginDto);

        // 1. authentication token 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);

        // 2. authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
        // 2.1. CustomUserDetailsService.loadUserByUsername() 에서 이미 발급받은 토큰이 존재하는지 체크
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 해당 객체를 SecurityContextHolder에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // authentication 객체를 generateToken 메소드를 통해서 JWT Token을 생성
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            updateUserTokenInfo(loginId, tokenInfo);

            return tokenInfo;
        }
        catch (BadCredentialsException e) {
            throw new GlobalException(BAD_CREDENTIAL);
        }
        catch (AuthenticationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AlreadyGetJwtTokenException) {
                return ((AlreadyGetJwtTokenException) cause).getTokenInfo();
            }
            else if (cause instanceof GlobalException){
                throw (GlobalException) cause;
            }
            else {
                throw e;
            }
        }
    }

    @CachePut(key = "#username")
    public void updateUserTokenInfo(String username, TokenInfo tokenInfo) {
        log.debug("[{}] : issue new token", username);

        // JWT token 정보 update
        MatchMaker matchMaker = getMatchMakerForAuthentication(username);
        matchMaker.updateJwtToken(tokenInfo);
    }

    @CachePut(key = "#loginDto.loginId")
    public void updateUserLastLoginInfo(LoginDto loginDto) {
        log.debug("[{}] : attempt to login from [{}]", loginDto.getLoginId(), current.getClientIp());
        MatchMaker matchMaker = getMatchMakerForAuthentication(loginDto.getLoginId());
        matchMaker.updateLastLoginIp(current.getClientIp());
    }

    public void authentication(String accessToken, String username) {
        if (accessToken == null) {
            return;
        }

        // 1. 토큰 유효성 검사
        jwtTokenProvider.validateAccessToken(accessToken);

        // 2. 인증 정보 추출 및 Context 등록
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        /**
         * 3. 로그인 로직
         * 3.1. jwt access token으로부터 principal get
         * 3.2. ruquest header로부터 받은 username과 비교해 본인의 토큰을 사용한 접근인지 확인
         */
        User principal = (User) authentication.getPrincipal();
        if (!username.equals(principal.getUsername())) {
            throw new NotAuthenticatedException(AUTHENTICATION_FAILED);
        }
    }

    @Cacheable(key="#loginId")
    public MatchMaker getMatchMakerForAuthentication(String loginId) {

        MatchMaker matchMaker = query.selectFrom(QMatchMaker.matchMaker)
                .where(QMatchMaker.matchMaker.loginId.eq(loginId))
                .fetchOne();

        if (matchMaker == null) {
           throw new GlobalException(USER_NOT_FOUND);
        }

        return matchMaker;
    }
}
