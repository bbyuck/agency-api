package com.ndm.core.domain.user.repository;


import com.ndm.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {

    User findByOauthId(String oauthId);
}
