package com.ndm.core.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ndm.core.common.enums.MemberCode.MATCH_MAKER;
import static com.ndm.core.common.enums.MemberCode.USER;

@Getter
@RequiredArgsConstructor
public enum ClientMessageCode {
    REQUEST_RECEIVED("요청을 받았습니다.", "상대의 프로필을 확인해주세요.", USER),
    REQUEST_REJECTED("요청이 거절되었습니다.", "", USER),
    REQUEST_ACCEPTED("상대가 요청을 수락했어요!", "상대의 사진을 확인하고 매칭을 완료해주세요.", USER),
    U_MATCHING_SUCCESS("상대도 소개 받기를 원해요!", "상대의 주선자에게 번호를 요청했습니다. 잠시만 기다려주세요.", USER),
    MATCHING_CANCEL("상대가 매칭을 취소했습니다.", "", USER),
    M_MATCHING_SUCCESS("커플 매칭에 성공했습니다!", "매칭 정보를 확인하고 소개 받으시는 분의 연락처를 전달해주세요.", MATCH_MAKER),;

    private final String title;
    private final String body;
    private final MemberCode memberCode;
}
