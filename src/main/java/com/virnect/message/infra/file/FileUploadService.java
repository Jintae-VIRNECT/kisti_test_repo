package com.virnect.base.infra.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * Project: service-server
 * DATE: 2019-10-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: File Upload Interface for LocalFileUpload and AWS FileUpload
 */
public interface FileUploadService {
    /**
     * 파일 업로드 처리
     * @param file -  파일 업로드 요청
     * @return - 업로드된 파일 url
     */
    String upload(MultipartFile file);

    /**
     * 업로드 된 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    void delete(final String url);

    /**
     *  업로드 요청 파일 확장자 추출
     * @param originFileName - 업로드 요청 파일 원본 이름
     * @return - 업로드 요청 파일 확장자
     */
    String getFileExtension(final String originFileName);

    /**
     * 업로드 요청 파일 확장자 허용 여부 검사
     * @param fileExtension - 업로드 요청 파일 확장자
     * @return - 허용 여부
     */
    boolean isAllowFileExtension(String fileExtension);
}
