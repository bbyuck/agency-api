package com.ndm.core.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSummaryDto {
    private Long id;
    private String age;
    private String address;
    private String job;
    private int height;
    private boolean smoking;
}
