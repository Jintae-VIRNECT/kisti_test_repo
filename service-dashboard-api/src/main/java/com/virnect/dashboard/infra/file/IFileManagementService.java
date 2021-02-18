package com.virnect.dashboard.infra.file;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import com.virnect.dashboard.domain.files.FileType;

public interface IFileManagementService {

    String LOG_MESSAGE_TAG = "[FILE MANAGEMENT SERVICE]::";

    int EXPIRE_DAY = 7; // 7 days expire time
    List<String> FILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF");
    List<String> FILE_DOCUMENT_ALLOW_EXTENSION = Arrays.asList("doc", "ppt", "xls", "dot", "docx", "xlsx", "pptx", "pdf");
    List<String> FILE_VIDEO_ALLOW_EXTENSION = Arrays.asList("mp4", "webm");

    boolean removeObject(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * 업로드 된 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    boolean delete(final String url);

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

    String filePreSignedUrl(
        final String dirPath,
        final String objectName,
        int expiry,
        String fileName,
        FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

}
