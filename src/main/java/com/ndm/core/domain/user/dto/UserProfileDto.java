package com.ndm.core.domain.user.dto;

import com.ndm.core.common.enums.Gender;
import com.ndm.core.common.enums.MBTI;
import com.ndm.core.domain.file.dto.FileResponseDto;
import com.ndm.core.domain.file.dto.PhotoData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileDto {
    private Gender gender;
    private String age;
    private String address;
    private String job;
    private Integer height;
    private String idealType;
    private String hobby;
    private MBTI mbti;
    private boolean smoking;
    private String selfDescription;
    private FileResponseDto photoData;
}
