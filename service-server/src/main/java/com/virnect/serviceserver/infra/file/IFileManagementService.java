package com.virnect.serviceserver.infra.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public interface IFileManagementService {

    String LOG_MESSAGE_TAG = "[FILE MANAGEMENT SERVICE]::";
    //String PROFILE_DIRECTORY = "profile";
    //String FILE_DIRECTORY = "files";


    int EXPIRE_DAY = 7; // 7 days expire time
    List<String> FILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF");
    List<String> FILE_DOCUMENT_ALLOW_EXTENSION = Arrays.asList("doc", "ppt", "xls", "dot", "docx", "xlsx", "pptx", "pdf");
    List<String> FILE_VIDEO_ALLOW_EXTENSION = Arrays.asList("mp4", "webm");
    List<String> FILE_ALLOW_EXTENSION = new ArrayList<>();
    /**
     * 파일 업로드 처리
     * @param file - 파일 업로드 요청
     * @return - 업로드된 파일 url
     */
    String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException;


    /**
     * 파일 업로드 처리
     * @param file - 업로드 요청 파일
     * @param dirPath - 파일 저장경로
     * @return - 업로드된 파일 url
     * @throws IOException
     */
    String upload(MultipartFile file, String dirPath) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    String uploadProfile(MultipartFile file, String dirPath) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    String uploadFile(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    String uploadPolicyFile(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * 업로드 된 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    boolean delete(final String url);

    /**
     * 업로드 요청 파일 확장자 추출
     *
     * @param originFileName - 업로드 요청 파일 원본 이름
     * @return - 업로드 요청 파일 확장자
     */
    String getFileExtension(final String originFileName);

    /**
     * 업로드 요청 파일 확장자 허용 여부 검사
     *
     * @param fileExtension - 업로드 요청 파일 확장자
     * @return - 허용 여부
     */
    boolean isAllowFileExtension(String fileExtension);

    /**
     * 파일 읽어들이기
     *
     * @param url - 파일이 저장된 경로
     * @return - 파일 데이터
     */
    File getFile(final String url);

    /**
     * base64로 인코딩된 이미지 파일 저장
     *
     * @param base64Image
     * @return
     */
    String base64ImageUpload(final String base64Image);

    byte[] fileDownload(final String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    void copyFileS3ToLocal(String fileName);
}
