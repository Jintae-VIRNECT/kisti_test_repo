package com.virnect.data.infra.file;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.virnect.data.domain.file.FileType;
import com.virnect.data.dto.UploadResult;

public interface IFileManagementService {
    String DEFAULT_ROOM_PROFILE = "default";

    int EXPIRE_DAY = 7; // 7 days expire time
    long MAX_USER_PROFILE_IMAGE_SIZE = 1048576 * 5; // 5MB in binary
    long MAX_FILE_SIZE = 1048576 * 20; // 20MB in binary

    //default file extension list
    List<String> FILE_PROFILE_ALLOW_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF");
    List<String> FILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF");
    List<String> FILE_DOCUMENT_ALLOW_EXTENSION = Arrays.asList("doc", "ppt", "xls", "dot", "docx", "xlsx", "pptx", "pdf");
    List<String> FILE_VIDEO_ALLOW_EXTENSION = Arrays.asList("mp4", "webm");

    void loadStoragePolicy();

    /**
     * File Upload
     *
     * @param file - 업로드 요청 파일
     * @param dirPath - 파일 저장경로
     * @return - 업로드된 파일 url
     * @throws IOException
     */
    UploadResult upload(MultipartFile file, String dirPath, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    UploadResult upload(MultipartFile file, String dirPath, FileType fileType, String objectName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    UploadResult uploadProfile(MultipartFile file, String dirPath) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    boolean removeObject(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * 업로드 된 프로필 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    void deleteProfile(final String url) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    void removeBucket(String bucketName, String dirPath, List<String> objects, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

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

    String filePreSignedUrl(
            final String bucketFolderName,
            final String objectName,
            int expiry) throws IOException, NoSuchAlgorithmException, InvalidKeyException;
}
