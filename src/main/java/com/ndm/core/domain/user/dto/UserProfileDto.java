package com.ndm.core.domain.user.dto;

import com.ndm.core.common.enums.Gender;
import com.ndm.core.common.enums.MBTI;
import com.ndm.core.domain.file.dto.FileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private boolean exist = true;

    private Long id;
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
