package com.ndm.core.domain.file.service;

import com.ndm.core.domain.file.dto.FileInfoDto;
import com.ndm.core.domain.file.repository.FileRepository;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Photo;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final UserRepository userRepository;

    private final Current current;

    @Value("${file.root}")
    private String fileRoot;

    private String getTodayString() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        StringBuilder sb = new StringBuilder();

        sb.append(year);
        if (month < 10) sb.append(0);
        sb.append(month);
        if (day < 10) sb.append(0);
        sb.append(day);

        return sb.toString();
    }

    private String getFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(fileRoot).append("/").append(getTodayString()).append("/").append(current.getUserCredentialToken()).append("/");
        
        return sb.toString();
    }

    private String getFileExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return split[split.length - 1];
    }

    public void upload(MultipartFile file) {
        if (file.getOriginalFilename() == null) {
            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
        }

        String ext = getFileExtension(file.getOriginalFilename());
        if (!Photo.EXTENTIONS.contains(ext)) {
            throw new GlobalException(ErrorInfo.NOT_SUPPORTED_FILE_EXTENSION);
        }

        Optional<User> optional = userRepository.findByUserToken(current.getUserCredentialToken());
        if (optional.isEmpty()) {
            log.error("잘못된 유저 credential token입니다.");
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }
        User owner = optional.get();

//        // 1. db 체크
        List<Photo> ownPhotoList = fileRepository.findByOwner(owner);
//
//        // 2. 물리파일 존재여부 체크
//        for (Photo photo : ownPhotoList) {
//            // 1. db에 존재하는데 물리파일이 없을 경우
//            // 해당 파일로 갈아치운다
//
//
//        }


        String filePath = getFilePath();
        String fullFilePath = filePath + "img_" + (ownPhotoList.size() + 1) + "." + getFileExtension(file.getOriginalFilename());


        writeFile(file, filePath, fullFilePath);
        saveFileInfo(file, owner, fullFilePath);

//        try(FileOutputStream fos = new FileOutputStream()) {
//            byte[] data = file.getBytes();
//
//
//        }
//        catch(IOException e) {
//            log.error(e.getMessage(), e);
//            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
//        }
    }

    private FileInfoDto saveFileInfo(MultipartFile file, User owner, String fullFilePath) {
        Photo photo = Photo.builder()
                .filePath(fullFilePath)
                .owner(owner)
                .build();

        fileRepository.save(photo);

        return new FileInfoDto(photo.getId());
    }

    private void writeFile(MultipartFile file, String filePath, String fullFilePath) {
        File dir = new File(filePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(fullFilePath)){
            byte[] bytes = file.getBytes();

            fos.write(bytes);
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
        }
    }
}
