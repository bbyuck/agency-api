package com.ndm.core.domain.file.service;

import com.ndm.core.domain.file.dto.FileInfoDto;
import com.ndm.core.domain.file.dto.FileResponseDto;
import com.ndm.core.domain.file.dto.PhotoData;
import com.ndm.core.domain.file.repository.FileRepository;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Photo;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.ndm.core.entity.QPhoto.photo;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final UserRepository userRepository;

    private final JPAQueryFactory query;

    private final Current current;

    @Value("${file.root}")
    private String fileRoot;

    private String getTodayString(LocalDateTime now) {
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

    private String getFilePath(LocalDateTime now) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileRoot).append("/").append(getTodayString(now)).append("/").append(current.getMemberCredentialToken()).append("/");

        return sb.toString();
    }
    private String getCompressedFilePath(LocalDateTime now) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileRoot).append("/").append(getTodayString(now)).append("/").append(current.getMemberCredentialToken()).append("/comp/");
        return sb.toString();
    }

    private String getFileExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return split[split.length - 1];
    }

    public void upload(MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();

        if (file.getOriginalFilename() == null) {
            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
        }

        String ext = getFileExtension(file.getOriginalFilename());
        if (!Photo.EXTENTIONS.contains(ext.toLowerCase())) {
            throw new GlobalException(ErrorInfo.NOT_SUPPORTED_FILE_EXTENSION);
        }

        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error("잘못된 유저 credential token입니다.");
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }


        User owner = optional.get();

        String fileName = "img_" + now.getHour() + now.getMinute() + now.getSecond() + "." + ext;
        String compressedFilePath = getCompressedFilePath(now);
        String compressedFullFilePath = compressedFilePath + fileName;
        String filePath = getFilePath(now);
        String fullFilePath = filePath + fileName;


        writeFile(file, filePath, fullFilePath);
        compressImage(fullFilePath, compressedFilePath, compressedFullFilePath, ext);
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

        try (FileOutputStream fos = new FileOutputStream(fullFilePath)) {
            byte[] bytes = file.getBytes();

            fos.write(bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
        }
    }

    @Transactional(readOnly = true)
    public FileResponseDto getMyFileData() {
        List<Photo> callersPhotoList = query
                .select(photo)
                .from(photo)
                .where(photo.owner.userToken.eq(current.getMemberCredentialToken()))
                .fetch();

        FileResponseDto fileResponseDto = new FileResponseDto();

        callersPhotoList.forEach(photo -> {
            try (FileInputStream fis = new FileInputStream(getCompressedFilePath(photo.getFilePath()));
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int length;

                byte[] buffer = new byte[512];
                while ((length = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }

                fileResponseDto.getPhotoDataList().add(new PhotoData(photo.getId(), bos.toByteArray()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new GlobalException(ErrorInfo.FILE_GET);
            }
        });

        return fileResponseDto;
    }

    public FileResponseDto getFileData(User owner) {
        FileResponseDto fileResponseDto = new FileResponseDto();

        owner.getPhotos().forEach(photo -> {
            try (FileInputStream fis = new FileInputStream(getCompressedFilePath(photo.getFilePath()));
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int length;

                byte[] buffer = new byte[512];
                while ((length = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }

                fileResponseDto.getPhotoDataList().add(new PhotoData(photo.getId(), bos.toByteArray()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new GlobalException(ErrorInfo.FILE_GET);
            }
        });

        return fileResponseDto;
    }

    private void compressImage(String sourceFilePath, String targetFileDir, String targetFilePath, String ext) {
        File dir = new File(targetFileDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (InputStream is = new FileInputStream(sourceFilePath);
             OutputStream os = new FileOutputStream(targetFilePath);
             ImageOutputStream ios = ImageIO.createImageOutputStream(os);) {

            float quality = 0.4f; //0.1 ~ 1.0까지 압축되는 이미지의 퀄리티를 지정
            //숫자가 낮을수록 화질과 용량이 줄어든다.

            BufferedImage image = ImageIO.read(is);
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(ext);

            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");

            ImageWriter writer = (ImageWriter) writers.next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), param);

            writer.dispose();
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.FILE_UPLOAD);
        }
    }


    public FileResponseDto deleteFile(FileInfoDto fileInfoDto) {
        Photo deleteTargetEntity = query
                .select(photo)
                .from(photo)
                .where(
                        photo.owner.userToken.eq(current.getMemberCredentialToken())
                                .and(photo.id.eq(fileInfoDto.getId())
                                )
                )
                .fetchOne();

        File deleteTarget = new File(deleteTargetEntity.getFilePath());
        if (deleteTarget.exists()) {
            deleteTarget.delete();
        }
        File compressedDeleteTarget = new File(getCompressedFilePath(deleteTargetEntity.getFilePath()));

        if (compressedDeleteTarget.exists()) {
            compressedDeleteTarget.delete();
        }

        fileRepository.delete(deleteTargetEntity);

        return getMyFileData();
    }

    private String getCompressedFilePath(String filePath) {
        String[] split = filePath.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]).append("/");
        }
        sb.append("/comp/").append(split[split.length - 1]);

        return sb.toString();
    }
}
