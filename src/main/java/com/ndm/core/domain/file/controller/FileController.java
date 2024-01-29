package com.ndm.core.domain.file.controller;

import com.ndm.core.domain.file.dto.FileInfoDto;
import com.ndm.core.domain.file.dto.FileResponseDto;
import com.ndm.core.domain.file.service.FileService;
import com.ndm.core.model.Response;
import com.ndm.core.model.Trace;
import com.ndm.core.model.version.V1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Trace
    @PostMapping("/photo")
    public Response<Object> photoUpload(@RequestParam MultipartFile file) {
        fileService.upload(file);
        return Response.builder().build();
    }

    @Trace
    @GetMapping("/photo")
    public Response<FileResponseDto> getPhoto() {
        return Response.<FileResponseDto>builder()
                .data(fileService.getMyFileData())
                .build();
    }

    @Trace
    @DeleteMapping("/photo")
    public Response<FileResponseDto> removePhoto(@RequestBody FileInfoDto fileInfoDto) {
        return Response.<FileResponseDto>builder()
                .data(fileService.deleteFile(fileInfoDto))
                .build();
    }
}
