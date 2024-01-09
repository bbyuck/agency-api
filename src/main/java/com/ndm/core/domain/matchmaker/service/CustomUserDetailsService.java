package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import com.ndm.core.domain.matchmaker.entity.MatchMaker;
import com.ndm.core.model.exception.AlreadyGetJwtTokenException;
import com.ndm.core.model.exception.GlobalException;
import com.ndm.core.model.exception.NotAuthenticatedException;
import com.ndm.core.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ndm.core.model.ErrorInfo.USER_NOT_FOUND;

@Slf4j
@Service
@CacheConfig(cacheNames = "auth")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        MatchMaker matchMaker = authService.getMatchMakerForAuthentication(username);

        if (matchMaker == null) {
            throw new GlobalException(USER_NOT_FOUND);
        }

        // 이미 발급받은 토큰이 있는지 체크
        try {
            String accessToken = matchMaker.getAccessToken();
            String refreshToken = matchMaker.getRefreshToken();
            jwtTokenProvider.validateAccessToken(accessToken);
            throw new AlreadyGetJwtTokenException(TokenInfo
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());
        }
        catch (NotAuthenticatedException e) {
            /**
             * /token/issue api에서만 해당 로직 호출
             * 그 외 인증/인가가 필요한 api의 경우 토큰 validation 실패시 refresh 토큰을 통해 reissue 필요
             */
            log.debug("=====================================");
            log.debug("The token you are holding is invalid.");
            log.debug("I will try to issue a new one.");
            log.debug("=====================================");
            return createUser(matchMaker);
        }
    }

    private User createUser(MatchMaker matchMaker) {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add((GrantedAuthority) () -> matchMaker.getRole().name());

        return new User(matchMaker.getLoginId(), matchMaker.getPassword(), grantedAuthorities);
    }
}
