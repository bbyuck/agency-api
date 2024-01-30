package com.ndm.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorInfo {
    INTERNAL_SERVER_ERROR(500, null, "서버 에러입니다.\n관리자에게 문의해주세요."),
    /* =========== Authentication / Authorization 관련 에러 ==========*/
    NOT_FOUND(404, null, "리소스를 찾을 수 없습니다."),
    ID_TOKEN_EXPIRED(401, null, "ID 토큰이 만료되었습니다."),
    ACCESS_TOKEN_EXPIRED(401, null, "Access 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, null, "Refresh 토큰이 만료되었습니다."),
    NO_AUTH_INFO_TOKEN(401, null, "권한 정보가 없는 토큰입니다."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "잘못된 토큰입니다."),
    UNSUPPORTED_JWT(401, null, "지원하지 않는 JWT 토큰입니다."),
    EMPTY_CLAIMS(401, null, "JWT claims 값이 비어있습니다."),
    AUTHENTICATION_FAILED(401, null, "인증에 실패했습니다."),
    USER_NOT_FOUND(401, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    BAD_CREDENTIAL(401, null, "잘못된 ID/PW 입니다."),
    IP_ADDRESS_CHANGED(400, null, "마지막 로그인 IP와 다른 IP로부터 들어온 요청입니다."),
    INVALID_CREDENTIAL_TOKEN(401, "INVALID_CREDENTIAL_TOKEN", "잘못된 인증 토큰입니다."),
    INVALID_AUTHORIZATION_CODE(401, "INVALID_AUTHORIZATION_CODE", "잘못된 Authorization code 입니다."),
    /* =========== Authentication / Authorization 관련 에러 ==========*/

    /* =========== SignUp / Login 관련 에러 ==========*/
    REGISTERED_MEMBER(400, "MEMBER.ID", "이미 가입된 ID 입니다."),
    USED_MATCH_MAKER_NAME(400, "USED_MATCH_MAKER_NAME", "이미 사용중인 닉네임입니다."),
    INVALID_MATCH_MAKER_NAME_1(400, "INVALID_MATCH_MAKER_NAME", "닉네임은 한글, 영문, 숫자로만 구성되어야 합니다."),
    INVALID_MATCH_MAKER_NAME_2(400, "INVALID_MATCH_MAKER_NAME", "닉네임은 1글자 이상, 15자 이하여야 합니다."),

    INVALID_MATCH_MAKER_NAME_3(400, "INVALID_MATCH_MAKER_NAME", "주선자를 찾을 수 없습니다.\n닉네임을 다시 확인해주세요."),

    INVALID_LOGIN_ID_0(400, "LOGIN_ID", "ID를 입력해주세요."),
    INVALID_LOGIN_ID_1(400, "LOGIN_ID", "ID는 4자 이상, 10자 이하여야 합니다."),
    INVALID_LOGIN_ID_2(400, "LOGIN_ID", "ID는 영문으로 시작해야 합니다."),
    INVALID_LOGIN_ID_3(400, "LOGIN_ID", "ID는 영문, 숫자로만 구성되어야 합니다."),
    INVALID_PASSWORD_0(400, "PASSWORD", "Password를 입력해주세요."),
    INVALID_PASSWORD_1(400, "PASSWORD", "Password는 8자 이상, 14자 이하여야 합니다."),
    INVALID_PASSWORD_2(400, "PASSWORD", "Password는 영문, 숫자, !,@,#,$,%,^,&,* 만 허용됩니다."),
    INVALID_PASSWORD_3(400, "PASSWORD", "Password는 영문, 숫자, 특수문자가 반드시 각각 하나 이상씩 포함되어야 합니다."),
    INVALID_PASSWORD_4(400, "PASSWORD_CONFIRM", "Password와 Confirm이 일치하지 않습니다. 다시 확인해주세요."),
    INVALID_PASSWORD_CONFIRM_0(400, "PASSWORD_CONFIRM", "Password를 한 번 더 입력해주세요."),


    INVALID_MATCHMAKER_CODE(400, "INVALID_MATCHMAKER_CODE", "잘못된 주선자 코드를 입력했습니다."),
    MATCHMAKER_NOT_FOUND(400, "INVALID_MATCHMAKER_CODE", "해당 주선자를 찾을 수 없습니다."),
    INVALID_OAUTH_ID(400, "INVALID_OAUTH_ID", "잘못된 ID입니다.\n로그인을 다시 시도해주세요."),

    // ==================== 동의서 관련 exception =======================
    DO_NOT_AGREE(400, "DO_NOT_AGREE", "필수 이용 약관 중 동의하지 않은 항목이 존재합니다.\n관리자에게 문의해주세요."),
    AGREEMENT_CODE_DOES_NOT_SELECTED(400, "AGREEMENT_CODE_DOES_NOT_SELECTED", "동의서가 선택되지 않았습니다."),
    AGREEMENT_NOT_FOUND(500, "AGREEMENT_NOT_FOUND", "동의서를 찾지 못했습니다.\n관리자에게 문의해주세요."),

    // ==================== 파일 관련 exception ========================
    FILE_UPLOAD(500, "FILE_UPLOAD", "파일 업로드 중 문제가 발생했습니다.\n관리자에게 문의해주세요."),
    FILE_GET(500, "FILE_GET", "파일을 가져오는 중 문제가 발생했습니다.\n관리자에게 문의해주세요."),
    NOT_SUPPORTED_FILE_EXTENSION(400, "NOT_SUPPORTED_FILE_EXTENSION", "지원하지 않는 파일 확장자입니다."),

    // ==================== 프로필 만들기 관련 exception ==================
    GENDER_EMPTY(400, "GENDER_EMPTY", "성별이 입력되지 않았습니다."),
    AGE_EMPTY(400, "AGE_EMPTY", "나이가 입력되지 않았습니다."),
    ADDRESS_EMPTY(400, "ADDRESS_EMPTY", "사는 곳이 입력되지 않았습니다."),
    JOB_EMPTY(400, "JOB_EMPTY", "어떤 일을 하는지 입력되지 않았습니다."),
    HEIGHT_EMPTY(400, "HEIGHT_EMPTY", "키가 입력되지 않았습니다."),
    HOBBY_EMPTY(400, "HOBBY_EMPTY", "취미가 입력되지 않았습니다."),
    MBTI_EMPTY(400, "MBTI_EMPTY", "MBTI가 입력되지 않았습니다."),
    IDEAL_TYPE_EMPTY(400, "IDEAL_TYPE_EMPTY", "원하는 이성상이 입력되지 않았습니다."),
    SELF_DESCRIPTION_EMPTY(400, "SELF_DESCRIPTION_EMPTY", "자기소개가 입력되지 않았습니다."),
    PHOTO_INVALID_SIZE(500, "PHOTO_COUNT", "사진이 2장 이상, 5장 이하로 업로드 되지 않았습니다."),
    SMOKING_EMPTY(400, "SMOKING_EMPTY", "흡연 여부가 입력되지 않았습니다.")



    ;


    private final int httpStatus;
    private final String code;
    private final String message;
}
