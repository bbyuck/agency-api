package com.ndm.core.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationCode {
    REQUEST_RECEIVED("요청을 받았습니다.", "상대방의 프로필을 확인해주세요."),
    REQUEST_REJECTED("요청이 거절되었습니다.", ""),
    REQUEST_ACCEPTED("상대방이 요청을 수락했어요!", "상대방의 사진을 확인하고 매칭을 완료해주세요."),
    MATCH_SUCCESS("매칭 완료를 눌러주세요.", "오늘 중으로 매칭 완료를 누르지 않으시면 자동으로 매칭풀로 돌아갑니다.");

    private final String title;
    private final String body;
}
