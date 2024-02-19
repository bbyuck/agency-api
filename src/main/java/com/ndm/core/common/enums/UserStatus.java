package com.ndm.core.common.enums;

public enum UserStatus {
    TEMP,
    NEW,
    PROFILE_MAKING, // 유저 : 프로필 생성 상태
    WAIT,
    ACTIVE,             // 2. 유저 : 프로필 공개 / 주선자 : 본인 풀에 존재하는 유저 프로필 목록 다른 유저 및 주선자에게 공개
    MATCHING_WAIT,      // 요청을 보내고 매칭을 기다리는 상태
    REQUEST_REJECTED,   // 요청을 거절 당함
    REQUEST_RECEIVED,   // 요청을 받은 상태
    REQUEST_CONFIRMED,  // 요청을 확인한 상태

    MATCHING,   // 3. 유저 : 요청 및 매칭중 단계
    MATCHING_CONFIRMED, // 매칭 확인
    MATCHING_ACCEPTED,  // 매칭 수락
    MATCHING_CANCEL,
    COMPLETED,  //
    INACTIVE

}
