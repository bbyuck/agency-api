package com.ndm.core.common.enums;

public enum UserStatus {
    TEMP,       // 0. Member Type 정해지지 않은 임시 유저
    NEW,        // 1. 프로필 미작성
    ACTIVE,     // 2. 프로필 작성 / 프로필 공개
    INACTIVE,   // 3. 프로필 작성 / 프로필 미공개
    MATCHING,   // 4. 매칭 단계
    COMPLETED   // 5. 매칭 성공 / 프로필 미공개
}
