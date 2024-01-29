package com.ndm.core.domain.file.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FileResponseDto {

    private List<PhotoData> photoDataList = new ArrayList<>();

}
