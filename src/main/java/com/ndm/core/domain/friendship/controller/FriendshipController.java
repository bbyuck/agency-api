package com.ndm.core.domain.friendship.controller;

import com.ndm.core.domain.friendship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
}
