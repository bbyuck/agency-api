package com.ndm.core.domain.file.controller;

import com.ndm.core.domain.file.service.FileService;
import com.ndm.core.model.Trace;
import com.ndm.core.model.version.V1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Trace
    @PostMapping("/photo/upload")
    public void photoUpload(@RequestParam MultipartFile file) throws Exception {
        fileService.upload(file);
    }
}
