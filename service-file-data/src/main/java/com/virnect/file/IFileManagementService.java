package com.virnect.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public interface IFileManagementService {

    String DEFAULT_ROOM_PROFILE = "default";

    String LOG_MESSAGE_TAG = "[FILE MANAGEMENT SERVICE]::";


    int EXPIRE_DAY = 7; // 7 days expire time
    List<String> FILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF");
    List<String> FILE_DOCUMENT_ALLOW_EXTENSION = Arrays.asList("doc", "ppt", "xls", "dot", "docx", "xlsx", "pptx", "pdf");
    List<String> FILE_VIDEO_ALLOW_EXTENSION = Arrays.asList("mp4", "webm");

    /**
     * File Upload
     *
     * @param file - 업로드 요청 파일
     * @param dirPath - 파일 저장경로
     * @return - 업로드된 파일 url
     * @throws IOException
     */
    String upload(MultipartFile file, String dirPath, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    String uploadProfile(MultipartFile file, String dirPath) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    boolean removeObject(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * 업로드 된 프로필 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    void deleteProfile(final String url) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * base64로 인코딩된 이미지 파일 저장
     *
     * @param base64Image
     * @return
     */
    String base64ImageUpload(final String base64Image);

    String filePreSignedUrl(
            final String dirPath,
            final String objectName,
            int expiry,
            String fileName,
            FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException;
}
