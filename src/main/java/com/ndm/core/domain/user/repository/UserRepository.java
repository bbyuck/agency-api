package com.ndm.core.domain.user.repository;


import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthCodeAndOauthId(OAuthCode oauthCode, String oauthId);

    Optional<User> findByUserToken(String userToken);
}
