package com.ndm.core.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhotoData {
    private Long id;
    private byte[] physicalData;
}
