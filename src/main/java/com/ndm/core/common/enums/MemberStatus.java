package com.ndm.core.common.enums;

public enum MemberStatus {
    TEMP,       // 0. 동의서 미동의 상태
    NEW,        // 1. 유저 / 주선자 : 동의서 동의 후 멤버 코드 선택 phase
    PROFILE_MAKING, // 유저 : 프로필 생성 상태
    ACTIVE,     // 2. 유저 : 프로필 공개 / 주선자 : 본인 풀에 존재하는 유저 프로필 목록 다른 유저 및 주선자에게 공개
    MATCHING,   // 3. 유저 : 요청 및 매칭중 단계
    INACTIVE,   // 3. 유저 : 프로필 미공개 (남성사 중간단계)
    COMPLETED   // 5. 매칭 성공 / 프로필 미공개
}