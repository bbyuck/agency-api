package com.ndm.core.domain.friendship.repository;

import com.ndm.core.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
