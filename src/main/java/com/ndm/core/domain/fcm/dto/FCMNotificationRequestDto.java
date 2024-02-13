package com.ndm.core.domain.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMNotificationRequestDto {

    private Long targetUserId;
    private String title;
    private String body;
    private String image;
    private Map<String, String> data;
}
